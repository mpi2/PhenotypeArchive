/* -*- mode: javascript; c-basic-offset: 4; indent-tabs-mode: nil -*- */

// 
// Dalliance Genome Explorer
// (c) Thomas Down 2006-2011
//
// bam.js: indexed binary alignments
//

var BAM_MAGIC = 21840194;
var BAI_MAGIC = 21578050;

function BamFile() {
}

function Vob(b, o) {
    this.block = b;
    this.offset = o;
}

Vob.prototype.toString = function() {
    return '' + this.block + ':' + this.offset;
}

function Chunk(minv, maxv) {
    this.minv = minv; this.maxv = maxv;
}

function makeBam(data, bai, callback) {
    var bam = new BamFile();
    bam.data = data;
    bam.bai = bai;

    bam.data.slice(0, 65536).fetch(function(r) {
        if (!r) {
            return dlog("Couldn't access BAM");
        }

        var unc = unbgzf(r);
        var uncba = new Uint8Array(unc);

        var magic = readInt(uncba, 0);
        var headLen = readInt(uncba, 4);
        var header = '';
        for (var i = 0; i < headLen; ++i) {
            header += String.fromCharCode(uncba[i + 8]);
        }

        var nRef = readInt(uncba, headLen + 8);
        var p = headLen + 12;

        bam.chrToIndex = {};
        bam.indexToChr = [];
        for (var i = 0; i < nRef; ++i) {
            var lName = readInt(uncba, p);
            var name = '';
            for (var j = 0; j < lName-1; ++j) {
                name += String.fromCharCode(uncba[p + 4 + j]);
            }
            var lRef = readInt(uncba, p + lName + 4);
            // dlog(name + ': ' + lRef);
            bam.chrToIndex[name] = i;
            if (name.indexOf('chr') == 0) {
                bam.chrToIndex[name.substring(3)] = i;
            } else {
                bam.chrToIndex['chr' + name] = i;
            }
            bam.indexToChr.push(name);

            p = p + 8 + lName;
        }

        if (bam.indices) {
            return callback(bam);
        }
    });

    bam.bai.fetch(function(header) {   // Do we really need to fetch the whole thing? :-(
        if (!header) {
            return dlog("Couldn't access BAI");
        }

        var uncba = new Uint8Array(header);
        var baiMagic = readInt(uncba, 0);
        if (baiMagic != BAI_MAGIC) {
            return dlog('Not a BAI file');
        }

        var nref = readInt(uncba, 4);

        bam.indices = [];

        var p = 8;
        for (var ref = 0; ref < nref; ++ref) {
            var blockStart = p;
            var nbin = readInt(uncba, p); p += 4;
            for (var b = 0; b < nbin; ++b) {
                var bin = readInt(uncba, p);
                var nchnk = readInt(uncba, p+4);
                p += 8 + (nchnk * 16);
            }
            var nintv = readInt(uncba, p); p += 4;
            p += (nintv * 8);
            if (nbin > 0) {
                bam.indices[ref] = new Uint8Array(header, blockStart, p - blockStart);
            }                     
        }
        if (bam.chrToIndex) {
            return callback(bam);
        }
    });
}



BamFile.prototype.blocksForRange = function(refId, min, max) {
    var index = this.indices[refId];
    if (!index) {
        return [];
    }

    var intBinsL = reg2bins(min, max);
    var intBins = [];
    for (var i = 0; i < intBinsL.length; ++i) {
        intBins[intBinsL[i]] = true;
    }
    var leafChunks = [], otherChunks = [];

    var nbin = readInt(index, 0);
    var p = 4;
    for (var b = 0; b < nbin; ++b) {
        var bin = readInt(index, p);
        var nchnk = readInt(index, p+4);
//        dlog('bin=' + bin + '; nchnk=' + nchnk);
        p += 8;
        if (intBins[bin]) {
            for (var c = 0; c < nchnk; ++c) {
                var cs = readVob(index, p);
                var ce = readVob(index, p + 8);
                (bin < 4681 ? otherChunks : leafChunks).push(new Chunk(cs, ce));
                p += 16;
            }
        } else {
            p +=  (nchnk * 16);
        }
    }
//    dlog('leafChunks = ' + miniJSONify(leafChunks));
//    dlog('otherChunks = ' + miniJSONify(otherChunks));

    var nintv = readInt(index, p);
    var lowest = null;
    var minLin = Math.min(min>>14, nintv - 1), maxLin = Math.min(max>>14, nintv - 1);
    for (var i = minLin; i <= maxLin; ++i) {
        var lb =  readVob(index, p + 4 + (i * 8));
        if (!lb) {
            continue;
        }
        if (!lowest || lb.block < lowest.block || lb.offset < lowest.offset) {
            lowest = lb;
        }
    }
    // dlog('Lowest LB = ' + lowest);
    
    var prunedOtherChunks = [];
    if (lowest != null) {
        for (var i = 0; i < otherChunks.length; ++i) {
            var chnk = otherChunks[i];
            if (chnk.maxv.block >= lowest.block && chnk.maxv.offset >= lowest.offset) {
                prunedOtherChunks.push(chnk);
            }
        }
    }
    // dlog('prunedOtherChunks = ' + miniJSONify(prunedOtherChunks));
    otherChunks = prunedOtherChunks;

    var intChunks = [];
    for (var i = 0; i < otherChunks.length; ++i) {
        intChunks.push(otherChunks[i]);
    }
    for (var i = 0; i < leafChunks.length; ++i) {
        intChunks.push(leafChunks[i]);
    }

    intChunks.sort(function(c0, c1) {
        var dif = c0.minv.block - c1.minv.block;
        if (dif != 0) {
            return dif;
        } else {
            return c0.minv.offset - c1.minv.offset;
        }
    });
    var mergedChunks = [];
    if (intChunks.length > 0) {
        var cur = intChunks[0];
        for (var i = 1; i < intChunks.length; ++i) {
            var nc = intChunks[i];
            if (nc.minv.block == cur.maxv.block /* && nc.minv.offset == cur.maxv.offset */) { // no point splitting mid-block
                cur = new Chunk(cur.minv, nc.maxv);
            } else {
                mergedChunks.push(cur);
                cur = nc;
            }
        }
        mergedChunks.push(cur);
    }
//    dlog('mergedChunks = ' + miniJSONify(mergedChunks));

    return mergedChunks;
}

BamFile.prototype.fetch = function(chr, min, max, callback) {
    var thisB = this;

    var chrId = this.chrToIndex[chr];
    var chunks;
    if (chrId === undefined) {
        chunks = [];
    } else {
        chunks = this.blocksForRange(chrId, min, max);
        if (!chunks) {
            callback(null, 'Error in index fetch');
        }
    }
    
    var records = [];
    var index = 0;
    var data;

    function tramp() {
        if (index >= chunks.length) {
            return callback(records);
        } else if (!data) {
            // dlog('fetching ' + index);
            var c = chunks[index];
            var fetchMin = c.minv.block;
            var fetchMax = c.maxv.block + (1<<16); // *sigh*
            thisB.data.slice(fetchMin, fetchMax - fetchMin).fetch(function(r) {
                data = unbgzf(r, c.maxv.block - c.minv.block + 1);
                return tramp();
            });
        } else {
            var ba = new Uint8Array(data);
            thisB.readBamRecords(ba, chunks[index].minv.offset, records, min, max, chrId);
            data = null;
            ++index;
            return tramp();
        }
    }
    tramp();
}

var SEQRET_DECODER = ['=', 'A', 'C', 'x', 'G', 'x', 'x', 'x', 'T', 'x', 'x', 'x', 'x', 'x', 'x', 'N'];
var CIGAR_DECODER = ['M', 'I', 'D', 'N', 'S', 'H', 'P', '=', 'X', '?', '?', '?', '?', '?', '?', '?'];

function BamRecord() {
}

BamFile.prototype.readBamRecords = function(ba, offset, sink, min, max, chrId) {
    while (true) {
        var blockSize = readInt(ba, offset);
        var blockEnd = offset + blockSize + 4;
        if (blockEnd >= ba.length) {
            return sink;
        }

        var record = new BamRecord();

        var refID = readInt(ba, offset + 4);
        var pos = readInt(ba, offset + 8);
        
        var bmn = readInt(ba, offset + 12);
        var bin = (bmn & 0xffff0000) >> 16;
        var mq = (bmn & 0xff00) >> 8;
        var nl = bmn & 0xff;

        var flag_nc = readInt(ba, offset + 16);
        var flag = (flag_nc & 0xffff0000) >> 16;
        var nc = flag_nc & 0xffff;
    
        var lseq = readInt(ba, offset + 20);
        
        var nextRef  = readInt(ba, offset + 24);
        var nextPos = readInt(ba, offset + 28);
        
        var tlen = readInt(ba, offset + 32);
    
        var readName = '';
        for (var j = 0; j < nl-1; ++j) {
            readName += String.fromCharCode(ba[offset + 36 + j]);
        }
    
        var p = offset + 36 + nl;

        var cigar = '';
        for (var c = 0; c < nc; ++c) {
            var cigop = readInt(ba, p);
            cigar = cigar + (cigop>>4) + CIGAR_DECODER[cigop & 0xf];
            p += 4;
        }
        record.cigar = cigar;
    
        var seq = '';
        var seqBytes = (lseq + 1) >> 1;
        for (var j = 0; j < seqBytes; ++j) {
            var sb = ba[p + j];
            seq += SEQRET_DECODER[(sb & 0xf0) >> 4];
            seq += SEQRET_DECODER[(sb & 0x0f)];
        }
        p += seqBytes;
        record.seq = seq;

        var qseq = '';
        for (var j = 0; j < lseq; ++j) {
            qseq += String.fromCharCode(ba[p + j]);
        }
        p += lseq;
        record.quals = qseq;
        
        record.pos = pos;
        record.mq = mq;
        record.readName = readName;
        record.segment = this.indexToChr[refID];

        while (p < blockEnd) {
            var tag = String.fromCharCode(ba[p]) + String.fromCharCode(ba[p + 1]);
            var type = String.fromCharCode(ba[p + 2]);
            var value;

            if (type == 'A') {
                value = String.fromCharCode(ba[p + 3]);
                p += 4;
            } else if (type == 'i' || type == 'I') {
                value = readInt(ba, p + 3);
                p += 7;
            } else if (type == 'c' || type == 'C') {
                value = ba[p + 3];
                p += 4;
            } else if (type == 's' || type == 'S') {
                value = readShort(ba, p + 3);
                p += 5;
            } else if (type == 'f') {
                throw 'FIXME need floats';
            } else if (type == 'Z') {
                p += 3;
                value = '';
                for (;;) {
                    var cc = ba[p++];
                    if (cc == 0) {
                        break;
                    } else {
                        value += String.fromCharCode(cc);
                    }
                }
            } else {
                throw 'Unknown type '+ type;
            }
            record[tag] = value;
        }

        if (!min || record.pos <= max && record.pos + lseq >= min) {
            if (chrId === undefined || refID == chrId) {
                sink.push(record);
            }
        }
        offset = blockEnd;
    }

    // Exits via top of loop.
}

function readInt(ba, offset) {
    return (ba[offset + 3] << 24) | (ba[offset + 2] << 16) | (ba[offset + 1] << 8) | (ba[offset]);
}

function readShort(ba, offset) {
    return (ba[offset + 1] << 8) | (ba[offset]);
}

function readVob(ba, offset) {
    var block = ((ba[offset+6] & 0xff) * 0x100000000) + ((ba[offset+5] & 0xff) * 0x1000000) + ((ba[offset+4] & 0xff) * 0x10000) + ((ba[offset+3] & 0xff) * 0x100) + ((ba[offset+2] & 0xff));
    var bint = (ba[offset+1] << 8) | (ba[offset]);
    if (block == 0 && bint == 0) {
        return null;  // Should only happen in the linear index?
    } else {
        return new Vob(block, bint);
    }
}

function unbgzf(data, lim) {
    lim = Math.min(lim || 1, data.byteLength - 100);
    var oBlockList = [];
    var ptr = [0];
    var totalSize = 0;

    while (ptr[0] < lim) {
        var ba = new Uint8Array(data, ptr[0], 100); // FIXME is this enough for all credible BGZF block headers?
        var xlen = (ba[11] << 8) | (ba[10]);
        // dlog('xlen[' + (ptr[0]) +']=' + xlen);
        var unc = jszlib_inflate_buffer(data, 12 + xlen + ptr[0], Math.min(65536, data.byteLength - 12 - xlen - ptr[0]), ptr);
        ptr[0] += 8;
        totalSize += unc.byteLength;
        oBlockList.push(unc);
    }

    if (oBlockList.length == 1) {
        return oBlockList[0];
    } else {
        var out = new Uint8Array(totalSize);
        var cursor = 0;
        for (var i = 0; i < oBlockList.length; ++i) {
            var b = new Uint8Array(oBlockList[i]);
            arrayCopy(b, 0, out, cursor, b.length);
            cursor += b.length;
        }
        return out.buffer;
    }
}

//
// Binning (transliterated from SAM1.3 spec)
//

/* calculate bin given an alignment covering [beg,end) (zero-based, half-close-half-open) */
function reg2bin(beg, end)
{
    --end;
    if (beg>>14 == end>>14) return ((1<<15)-1)/7 + (beg>>14);
    if (beg>>17 == end>>17) return ((1<<12)-1)/7 + (beg>>17);
    if (beg>>20 == end>>20) return ((1<<9)-1)/7 + (beg>>20);
    if (beg>>23 == end>>23) return ((1<<6)-1)/7 + (beg>>23);
    if (beg>>26 == end>>26) return ((1<<3)-1)/7 + (beg>>26);
    return 0;
}

/* calculate the list of bins that may overlap with region [beg,end) (zero-based) */
var MAX_BIN = (((1<<18)-1)/7);
function reg2bins(beg, end) 
{
    var i = 0, k, list = [];
    --end;
    list.push(0);
    for (k = 1 + (beg>>26); k <= 1 + (end>>26); ++k) list.push(k);
    for (k = 9 + (beg>>23); k <= 9 + (end>>23); ++k) list.push(k);
    for (k = 73 + (beg>>20); k <= 73 + (end>>20); ++k) list.push(k);
    for (k = 585 + (beg>>17); k <= 585 + (end>>17); ++k) list.push(k);
    for (k = 4681 + (beg>>14); k <= 4681 + (end>>14); ++k) list.push(k);
    return list;
}/* -*- mode: javascript; c-basic-offset: 4; indent-tabs-mode: nil -*- */

// 
// Dalliance Genome Explorer
// (c) Thomas Down 2006-2010
//
// bigwig.js: indexed binary WIG (and BED) files
//

var BIG_WIG_MAGIC = -2003829722;
var BIG_BED_MAGIC = -2021002517;

var BIG_WIG_TYPE_GRAPH = 1;
var BIG_WIG_TYPE_VSTEP = 2;
var BIG_WIG_TYPE_FSTEP = 3;
    
function BigWig() {
}

BigWig.prototype.readChromTree = function(callback) {
    var thisB = this;
    this.chromsToIDs = {};
    this.idsToChroms = {};

    var udo = this.unzoomedDataOffset;
    while ((udo % 4) != 0) {
        ++udo;
    }

    this.data.slice(this.chromTreeOffset, udo - this.chromTreeOffset).fetch(function(bpt) {
        var ba = new Uint8Array(bpt);
        var sa = new Int16Array(bpt);
        var la = new Int32Array(bpt);
        var bptMagic = la[0];
        var blockSize = la[1];
        var keySize = la[2];
        var valSize = la[3];
        var itemCount = (la[4] << 32) | (la[5]);
        var rootNodeOffset = 32;
        
        // dlog('blockSize=' + blockSize + '    keySize=' + keySize + '   valSize=' + valSize + '    itemCount=' + itemCount);

        var bptReadNode = function(offset) {
            var nodeType = ba[offset];
            var cnt = sa[(offset/2) + 1];
            // dlog('ReadNode: ' + offset + '     type=' + nodeType + '   count=' + cnt);
            offset += 4;
            for (var n = 0; n < cnt; ++n) {
                if (nodeType == 0) {
                    offset += keySize;
                    var childOffset = (la[offset/4] << 32) | (la[offset/4 + 1]);
                    offset += 8;
                    childOffset -= thisB.chromTreeOffset;
                    bptReadNode(childOffset);
                } else {
                    var key = '';
                    for (var ki = 0; ki < keySize; ++ki) {
                        var charCode = ba[offset++];
                        if (charCode != 0) {
                            key += String.fromCharCode(charCode);
                        }
                    }
                    var chromId = (ba[offset+3]<<24) | (ba[offset+2]<<16) | (ba[offset+1]<<8) | (ba[offset+0]);
                    var chromSize = (ba[offset + 7]<<24) | (ba[offset+6]<<16) | (ba[offset+5]<<8) | (ba[offset+4]);
                    offset += 8;

                    // dlog(key + ':' + chromId + ',' + chromSize);
                    thisB.chromsToIDs[key] = chromId;
                    if (key.indexOf('chr') == 0) {
                        thisB.chromsToIDs[key.substr(3)] = chromId;
                    }
                    thisB.idsToChroms[chromId] = key;
                }
            }
        };
        bptReadNode(rootNodeOffset);

        callback(thisB);
    });
}

function BigWigView(bwg, cirTreeOffset, cirTreeLength, isSummary) {
    this.bwg = bwg;
    this.cirTreeOffset = cirTreeOffset;
    this.cirTreeLength = cirTreeLength;
    this.isSummary = isSummary;
}

BED_COLOR_REGEXP = new RegExp("^[0-9]+,[0-9]+,[0-9]+");

BigWigView.prototype.readWigData = function(chrName, min, max, callback) {
    var chr = this.bwg.chromsToIDs[chrName];
    if (chr === undefined) {
        // Not an error because some .bwgs won't have data for all chromosomes.

        // dlog("Couldn't find chr " + chrName);
        // dlog('Chroms=' + miniJSONify(this.bwg.chromsToIDs));
        return callback([]);
    } else {
        this.readWigDataById(chr, min, max, callback);
    }
}

BigWigView.prototype.readWigDataById = function(chr, min, max, callback) {
    var thisB = this;
    if (!this.cirHeader) {
        // dlog('No CIR yet, fetching');
        this.bwg.data.slice(this.cirTreeOffset, 48).fetch(function(result) {
            thisB.cirHeader = result;
            var la = new Int32Array(thisB.cirHeader);
            thisB.cirBlockSize = la[1];
            thisB.readWigDataById(chr, min, max, callback);
        });
        return;
    }

    var blocksToFetch = [];
    var outstanding = 0;

    var beforeBWG = Date.now();

    var cirFobRecur = function(offset, level) {
        outstanding += offset.length;

        var maxCirBlockSpan = 4 +  (thisB.cirBlockSize * 32);   // Upper bound on size, based on a completely full leaf node.
        var spans;
        for (var i = 0; i < offset.length; ++i) {
            var blockSpan = new Range(offset[i], Math.min(offset[i] + maxCirBlockSpan, thisB.cirTreeOffset + thisB.cirTreeLength));
            spans = spans ? union(spans, blockSpan) : blockSpan;
        }
        
        var fetchRanges = spans.ranges();
        // dlog('fetchRanges: ' + fetchRanges);
        for (var r = 0; r < fetchRanges.length; ++r) {
            var fr = fetchRanges[r];
            cirFobStartFetch(offset, fr, level);
        }
    }

    var cirFobStartFetch = function(offset, fr, level, attempts) {
        var length = fr.max() - fr.min();
        // dlog('fetching ' + fr.min() + '-' + fr.max() + ' (' + (fr.max() - fr.min()) + ')');
        thisB.bwg.data.slice(fr.min(), fr.max() - fr.min()).fetch(function(resultBuffer) {
            for (var i = 0; i < offset.length; ++i) {
                if (fr.contains(offset[i])) {
                    cirFobRecur2(resultBuffer, offset[i] - fr.min(), level);
                    --outstanding;
                    if (outstanding == 0) {
                        cirCompleted();
                    }
                }
            }
        });
    }

    var cirFobRecur2 = function(cirBlockData, offset, level) {
        var ba = new Int8Array(cirBlockData);
        var sa = new Int16Array(cirBlockData);
        var la = new Int32Array(cirBlockData);

        var isLeaf = ba[offset];
        var cnt = sa[offset/2 + 1];
        // dlog('cir level=' + level + '; cnt=' + cnt);
        offset += 4;

        if (isLeaf != 0) {
            for (var i = 0; i < cnt; ++i) {
                var lo = offset/4;
                var startChrom = la[lo];
                var startBase = la[lo + 1];
                var endChrom = la[lo + 2];
                var endBase = la[lo + 3];
                var blockOffset = (la[lo + 4]<<32) | (la[lo + 5]);
                var blockSize = (la[lo + 6]<<32) | (la[lo + 7]);
                if ((startChrom < chr || (startChrom == chr && startBase <= max)) &&
                    (endChrom   > chr || (endChrom == chr && endBase >= min)))
                {
                    // dlog('Got an interesting block: startBase=' + startBase + '; endBase=' + endBase + '; offset=' + blockOffset + '; size=' + blockSize);
                    blocksToFetch.push({offset: blockOffset, size: blockSize});
                }
                offset += 32;
            }
        } else {
            var recurOffsets = [];
            for (var i = 0; i < cnt; ++i) {
                var lo = offset/4;
                var startChrom = la[lo];
                var startBase = la[lo + 1];
                var endChrom = la[lo + 2];
                var endBase = la[lo + 3];
                var blockOffset = (la[lo + 4]<<32) | (la[lo + 5]);
                if ((startChrom < chr || (startChrom == chr && startBase <= max)) &&
                    (endChrom   > chr || (endChrom == chr && endBase >= min)))
                {
                    recurOffsets.push(blockOffset);
                }
                offset += 24;
            }
            if (recurOffsets.length > 0) {
                cirFobRecur(recurOffsets, level + 1);
            }
        }
    };
    

    var cirCompleted = function() {
        blocksToFetch.sort(function(b0, b1) {
            return (b0.offset|0) - (b1.offset|0);
        });

        if (blocksToFetch.length == 0) {
            callback([]);
        } else {
            var features = [];
            var createFeature = function(fmin, fmax, opts) {
                // dlog('createFeature(' + fmin +', ' + fmax + ')');

                if (!opts) {
                    opts = {};
                }
            
                var f = new DASFeature();
                f.segment = thisB.bwg.idsToChroms[chr];
                f.min = fmin;
                f.max = fmax;
                f.type = 'bigwig';
                
                for (k in opts) {
                    f[k] = opts[k];
                }
                
                features.push(f);
            };
            var maybeCreateFeature = function(fmin, fmax, opts) {
                if (fmin <= max && fmax >= min) {
                    createFeature(fmin, fmax, opts);
                }
            };
            var tramp = function() {
                if (blocksToFetch.length == 0) {
                    var afterBWG = Date.now();
                    // dlog('BWG fetch took ' + (afterBWG - beforeBWG) + 'ms');
                    callback(features);
                    return;  // just in case...
                } else {
                    var block = blocksToFetch[0];
                    if (block.data) {
                        var ba = new Uint8Array(block.data);

                        if (thisB.isSummary) {
                            var sa = new Int16Array(block.data);
                            var la = new Int32Array(block.data);
                            var fa = new Float32Array(block.data);

                            var itemCount = block.data.byteLength/32;
                            for (var i = 0; i < itemCount; ++i) {
                                var chromId =   la[(i*8)];
                                var start =     la[(i*8)+1];
                                var end =       la[(i*8)+2];
                                var validCnt =  la[(i*8)+3];
                                var minVal    = fa[(i*8)+4];
                                var maxVal    = fa[(i*8)+5];
                                var sumData   = fa[(i*8)+6];
                                var sumSqData = fa[(i*8)+7];
                                
                                if (chromId == chr) {
                                    var summaryOpts = {type: 'bigwig', score: sumData/validCnt};
                                    if (thisB.bwg.type == 'bigbed') {
                                        summaryOpts.type = 'density';
                                    }
                                    maybeCreateFeature(start, end, summaryOpts);
                                }
                            }
                        } else if (thisB.bwg.type == 'bigwig') {
                            var sa = new Int16Array(block.data);
                            var la = new Int32Array(block.data);
                            var fa = new Float32Array(block.data);

                            var chromId = la[0];
                            var blockStart = la[1];
                            var blockEnd = la[2];
                            var itemStep = la[3];
                            var itemSpan = la[4];
                            var blockType = ba[20];
                            var itemCount = sa[11];

                            // dlog('processing bigwig block, type=' + blockType + '; count=' + itemCount);
                            
                            if (blockType == BIG_WIG_TYPE_FSTEP) {
                                for (var i = 0; i < itemCount; ++i) {
                                    var score = fa[i + 6];
                                    maybeCreateFeature(blockStart + (i*itemStep), blockStart + (i*itemStep) + itemSpan, {score: score});
                                }
                            } else if (blockType == BIG_WIG_TYPE_VSTEP) {
                                for (var i = 0; i < itemCount; ++i) {
                                    var start = la[(i*2) + 6];
                                    var score = fa[(i*2) + 7];
                                    maybeCreateFeature(start, start + itemSpan, {score: score});
                                }
                            } else if (blockType == BIG_WIG_TYPE_GRAPH) {
                                for (var i = 0; i < itemCount; ++i) {
                                    var start = la[(i*3) + 6] + 1;
                                    var end   = la[(i*3) + 7];
                                    var score = fa[(i*3) + 8];
                                    if (start > end) {
                                        start = end;
                                    }
                                    maybeCreateFeature(start, end, {score: score});
                                }
                            } else {
                                dlog('Currently not handling bwgType=' + blockType);
                            }
                        } else if (thisB.bwg.type == 'bigbed') {
                            var offset = 0;
                            while (offset < ba.length) {
                                var chromId = (ba[offset+3]<<24) | (ba[offset+2]<<16) | (ba[offset+1]<<8) | (ba[offset+0]);
                                var start = (ba[offset+7]<<24) | (ba[offset+6]<<16) | (ba[offset+5]<<8) | (ba[offset+4]);
                                var end = (ba[offset+11]<<24) | (ba[offset+10]<<16) | (ba[offset+9]<<8) | (ba[offset+8]);
                                offset += 12;
                                var rest = '';
                                while (true) {
                                    var ch = ba[offset++];
                                    if (ch != 0) {
                                        rest += String.fromCharCode(ch);
                                    } else {
                                        break;
                                    }
                                }

                                var featureOpts = {};
                                
                                var bedColumns = rest.split('\t');
                                if (bedColumns.length > 0) {
                                    featureOpts.label = bedColumns[0];
                                }
                                if (bedColumns.length > 1) {
                                    featureOpts.score = stringToInt(bedColumns[1]);
                                }
                                if (bedColumns.length > 2) {
                                    featureOpts.orientation = bedColumns[2];
                                }
                                if (bedColumns.length > 5) {
                                    var color = bedColumns[5];
                                    if (BED_COLOR_REGEXP.test(color)) {
                                        featureOpts.override_color = 'rgb(' + color + ')';
                                    }
                                }

                                if (bedColumns.length < 9) {
                                    if (chromId == chr) {
                                        maybeCreateFeature(start + 1, end, featureOpts);
                                    }
                                } else if (chromId == chr && start <= max && end >= min) {
                                    // Complex-BED?
                                    // FIXME this is currently a bit of a hack to do Clever Things with ensGene.bb

                                    var thickStart = bedColumns[3]|0;
                                    var thickEnd   = bedColumns[4]|0;
                                    var blockCount = bedColumns[6]|0;
                                    var blockSizes = bedColumns[7].split(',');
                                    var blockStarts = bedColumns[8].split(',');
                                    
                                    featureOpts.type = 'bb-transcript'
                                    var grp = new DASGroup();
                                    grp.id = bedColumns[0];
                                    grp.type = 'bb-transcript'
                                    grp.notes = [];
                                    featureOpts.groups = [grp];

                                    if (bedColumns.length > 10) {
                                        var geneId = bedColumns[9];
                                        var geneName = bedColumns[10];
                                        var gg = new DASGroup();
                                        gg.id = geneId;
                                        gg.label = geneName;
                                        gg.type = 'gene';
                                        featureOpts.groups.push(gg);
                                    }

                                    var spans = null;
                                    for (var b = 0; b < blockCount; ++b) {
                                        var bmin = (blockStarts[b]|0) + start;
                                        var bmax = bmin + (blockSizes[b]|0);
                                        var span = new Range(bmin, bmax);
                                        if (spans) {
                                            spans = union(spans, span);
                                        } else {
                                            spans = span;
                                        }
                                    }
                                    
                                    var tsList = spans.ranges();
                                    for (var s = 0; s < tsList.length; ++s) {
                                        var ts = tsList[s];
                                        createFeature(ts.min() + 1, ts.max(), featureOpts);
                                    }

                                    if (thickEnd > thickStart) {
                                        var tl = intersection(spans, new Range(thickStart, thickEnd));
                                        if (tl) {
                                            featureOpts.type = 'bb-translation';
                                            var tlList = tl.ranges();
                                            for (var s = 0; s < tlList.length; ++s) {
                                                var ts = tlList[s];
                                                createFeature(ts.min() + 1, ts.max(), featureOpts);
                                            }
                                        }
                                    }
                                }
                            }
                        } else {
                            dlog("Don't know what to do with " + thisB.bwg.type);
                        }
                        blocksToFetch.splice(0, 1);
                        tramp();
                    } else {
                        var fetchStart = block.offset;
                        var fetchSize = block.size;
                        var bi = 1;
                        while (bi < blocksToFetch.length && blocksToFetch[bi].offset == (fetchStart + fetchSize)) {
                            fetchSize += blocksToFetch[bi].size;
                            ++bi;
                        }

                        thisB.bwg.data.slice(fetchStart, fetchSize).fetch(function(result) {
                            var offset = 0;
                            var bi = 0;
                            while (offset < fetchSize) {
                                var fb = blocksToFetch[bi];
                            
                                var data;
                                if (thisB.bwg.uncompressBufSize > 0) {
                                    // var beforeInf = Date.now();
                                    data = jszlib_inflate_buffer(result, offset + 2, fb.size - 2);
                                    // var afterInf = Date.now();
                                    // dlog('inflate: ' + (afterInf - beforeInf) + 'ms');
                                } else {
                                    var tmp = new Uint8Array(fb.size);    // FIXME is this really the best we can do?
                                    arrayCopy(new Uint8Array(result, offset, fb.size), 0, tmp, 0, fb.size);
                                    data = tmp.buffer;
                                }
                                fb.data = data;
                                
                                offset += fb.size;
                                ++bi;
                            }
                            tramp();
                        });
                    }
                }
            }
            tramp();
        }
    }

    cirFobRecur([thisB.cirTreeOffset + 48], 1);
}

//
// nasty cut/paste, should roll back in!
//

BigWigView.prototype.getFirstAdjacent = function(chrName, pos, dir, callback) {
    var chr = this.bwg.chromsToIDs[chrName];
    if (chr === undefined) {
        // Not an error because some .bwgs won't have data for all chromosomes.

        // dlog("Couldn't find chr " + chrName);
        // dlog('Chroms=' + miniJSONify(this.bwg.chromsToIDs));
        return callback([]);
    } else {
        this.getFirstAdjacentById(chr, pos, dir, callback);
    }
}

BigWigView.prototype.getFirstAdjacentById = function(chr, pos, dir, callback) {
    var thisB = this;
    if (!this.cirHeader) {
        // dlog('No CIR yet, fetching');
        this.bwg.data.slice(this.cirTreeOffset, 48).fetch(function(result) {
            thisB.cirHeader = result;
            var la = new Int32Array(thisB.cirHeader);
            thisB.cirBlockSize = la[1];
            thisB.readWigDataById(chr, min, max, callback);
        });
        return;
    }

    var blockToFetch = null;
    var bestBlockChr = -1;
    var bestBlockOffset = -1;

    var outstanding = 0;

    var beforeBWG = Date.now();

    var cirFobRecur = function(offset, level) {
        outstanding += offset.length;

        var maxCirBlockSpan = 4 +  (thisB.cirBlockSize * 32);   // Upper bound on size, based on a completely full leaf node.
        var spans;
        for (var i = 0; i < offset.length; ++i) {
            var blockSpan = new Range(offset[i], Math.min(offset[i] + maxCirBlockSpan, thisB.cirTreeOffset + thisB.cirTreeLength));
            spans = spans ? union(spans, blockSpan) : blockSpan;
        }
        
        var fetchRanges = spans.ranges();
        // dlog('fetchRanges: ' + fetchRanges);
        for (var r = 0; r < fetchRanges.length; ++r) {
            var fr = fetchRanges[r];
            cirFobStartFetch(offset, fr, level);
        }
    }

    var cirFobStartFetch = function(offset, fr, level, attempts) {
        var length = fr.max() - fr.min();
        // dlog('fetching ' + fr.min() + '-' + fr.max() + ' (' + (fr.max() - fr.min()) + ')');
        thisB.bwg.data.slice(fr.min(), fr.max() - fr.min()).fetch(function(result) {
            var resultBuffer = result;

// This is now handled in URLFetchable instead.
//
//            if (resultBuffer.byteLength != length) {           
//                dlog("Didn't get expected size: " + resultBuffer.byteLength + " != " + length);
//                return cirFobStartFetch(offset, fr, level, attempts + 1);
//            }


            for (var i = 0; i < offset.length; ++i) {
                if (fr.contains(offset[i])) {
                    cirFobRecur2(resultBuffer, offset[i] - fr.min(), level);
                    --outstanding;
                    if (outstanding == 0) {
                        cirCompleted();
                    }
                }
            }
        });
    }

    var cirFobRecur2 = function(cirBlockData, offset, level) {
        var ba = new Int8Array(cirBlockData);
        var sa = new Int16Array(cirBlockData);
        var la = new Int32Array(cirBlockData);

        var isLeaf = ba[offset];
        var cnt = sa[offset/2 + 1];
        // dlog('cir level=' + level + '; cnt=' + cnt);
        offset += 4;

        if (isLeaf != 0) {
            for (var i = 0; i < cnt; ++i) {
                var lo = offset/4;
                var startChrom = la[lo];
                var startBase = la[lo + 1];
                var endChrom = la[lo + 2];
                var endBase = la[lo + 3];
                var blockOffset = (la[lo + 4]<<32) | (la[lo + 5]);
                var blockSize = (la[lo + 6]<<32) | (la[lo + 7]);
                // dlog('startChrom=' + startChrom);
                if ((dir < 0 && ((startChrom < chr || (startChrom == chr && startBase <= pos)))) ||
                    (dir > 0 && ((endChrom > chr || (endChrom == chr && endBase >= pos)))))
                {
                    // dlog('Got an interesting block: startBase=' + startChrom + ':' + startBase + '; endBase=' + endChrom + ':' + endBase + '; offset=' + blockOffset + '; size=' + blockSize);
                    if (/_random/.exec(thisB.bwg.idsToChroms[startChrom])) {
                        // dlog('skipping random: ' + thisB.bwg.idsToChroms[startChrom]);
                    } else if (blockToFetch == null || ((dir < 0) && (endChrom > bestBlockChr || (endChrom == bestBlockChr && endBase > bestBlockOffset)) ||
                                                 (dir > 0) && (startChrom < bestBlockChr || (startChrom == bestBlockChr && startBase < bestBlockOffset))))
                    {
                        //                        dlog('best is: startBase=' + startChrom + ':' + startBase + '; endBase=' + endChrom + ':' + endBase + '; offset=' + blockOffset + '; size=' + blockSize);
                        blockToFetch = {offset: blockOffset, size: blockSize};
                        bestBlockOffset = (dir < 0) ? endBase : startBase;
                        bestBlockChr = (dir < 0) ? endChrom : startChrom;
                    }
                }
                offset += 32;
            }
        } else {
            var bestRecur = -1;
            var bestPos = -1;
            var bestChr = -1;
            for (var i = 0; i < cnt; ++i) {
                var lo = offset/4;
                var startChrom = la[lo];
                var startBase = la[lo + 1];
                var endChrom = la[lo + 2];
                var endBase = la[lo + 3];
                var blockOffset = (la[lo + 4]<<32) | (la[lo + 5]);
                // dlog('startChrom=' + startChrom);
                if ((dir < 0 && ((startChrom < chr || (startChrom == chr && startBase <= pos)) &&
                                 (endChrom   >= chr))) ||
                     (dir > 0 && ((endChrom > chr || (endChrom == chr && endBase >= pos)) &&
                                  (startChrom <= chr))))
                {
                    // dlog('Got an interesting block: startBase=' + startChrom + ':' + startBase + '; endBase=' + endChrom + ':' + endBase + '; offset=' + blockOffset + '; size=' + blockSize);
                    if (bestRecur < 0 || endBase > bestPos) {
                        bestRecur = blockOffset;
                        bestPos = (dir < 0) ? endBase : startBase;
                        bestChr = (dir < 0) ? endChrom : startChrom;
                    }
                }
                offset += 24;
            }
            if (bestRecur >= 0) {
                cirFobRecur([bestRecur], level + 1);
            }
        }
    };
    

    var cirCompleted = function() {
        if (blockToFetch == null) {
            return dlog('got nothing');
        } 
        var blocksToFetch = [blockToFetch];

        blocksToFetch.sort(function(b0, b1) {
            return (b0.offset|0) - (b1.offset|0);
        });

        if (blocksToFetch.length == 0) {
            callback([]);
        } else {
            var bestFeature = null;
            var bestChr = -1;
            var bestPos = -1;
            var createFeature = function(chrx, fmin, fmax, opts) {
//                dlog('createFeature(' + fmin +', ' + fmax + ')');

                if (!opts) {
                    opts = {};
                }
            
                var f = new DASFeature();
                f.segment = thisB.bwg.idsToChroms[chrx];
                f.min = fmin;
                f.max = fmax;
                f.type = 'bigwig';
                
                for (k in opts) {
                    f[k] = opts[k];
                }
                
                if (bestFeature == null || ((dir < 0) && (chrx > bestChr || fmax > bestPos)) || ((dir > 0) && (chrx < bestChr || fmin < bestPos))) {
                    bestFeature = f;
                    bestPos = (dir < 0) ? fmax : fmin;
                    bestChr = chrx;
                }
            };
            var maybeCreateFeature = function(chrx, fmin, fmax, opts) {
//                dlog('maybeCreateFeature(' + thisB.bwg.idsToChroms[chrx] + ',' + fmin + ',' + fmax + ')');
                if ((dir < 0 && (chrx < chr || fmax < pos)) || (dir > 0 && (chrx > chr || fmin > pos))) {
                //                if (fmin <= max && fmax >= min) {
                    createFeature(chrx, fmin, fmax, opts);
                    //}
                }
            };
            var tramp = function() {
                if (blocksToFetch.length == 0) {
                    var afterBWG = Date.now();
                    // dlog('BWG fetch took ' + (afterBWG - beforeBWG) + 'ms');
                    callback([bestFeature]);
                    return;  // just in case...
                } else {
                    var block = blocksToFetch[0];
                    if (block.data) {
                        var ba = new Uint8Array(block.data);

                        if (thisB.isSummary) {
                            var sa = new Int16Array(block.data);
                            var la = new Int32Array(block.data);
                            var fa = new Float32Array(block.data);

                            var itemCount = block.data.byteLength/32;
                            for (var i = 0; i < itemCount; ++i) {
                                var chromId =   la[(i*8)];
                                var start =     la[(i*8)+1];
                                var end =       la[(i*8)+2];
                                var validCnt =  la[(i*8)+3];
                                var minVal    = fa[(i*8)+4];
                                var maxVal    = fa[(i*8)+5];
                                var sumData   = fa[(i*8)+6];
                                var sumSqData = fa[(i*8)+7];
                                
                                var summaryOpts = {type: 'bigwig', score: sumData/validCnt};
                                if (thisB.bwg.type == 'bigbed') {
                                    summaryOpts.type = 'density';
                                }
                                maybeCreateFeature(chromId, start, end, summaryOpts);
                            }
                        } else if (thisB.bwg.type == 'bigwig') {
                            var sa = new Int16Array(block.data);
                            var la = new Int32Array(block.data);
                            var fa = new Float32Array(block.data);

                            var chromId = la[0];
                            var blockStart = la[1];
                            var blockEnd = la[2];
                            var itemStep = la[3];
                            var itemSpan = la[4];
                            var blockType = ba[20];
                            var itemCount = sa[11];

                            // dlog('processing bigwig block, type=' + blockType + '; count=' + itemCount);
                            
                            if (blockType == BIG_WIG_TYPE_FSTEP) {
                                for (var i = 0; i < itemCount; ++i) {
                                    var score = fa[i + 6];
                                    maybeCreateFeature(chromId, blockStart + (i*itemStep), blockStart + (i*itemStep) + itemSpan, {score: score});
                                }
                            } else if (blockType == BIG_WIG_TYPE_VSTEP) {
                                for (var i = 0; i < itemCount; ++i) {
                                    var start = la[(i*2) + 6];
                                    var score = fa[(i*2) + 7];
                                    maybeCreateFeature(start, start + itemSpan, {score: score});
                                }
                            } else if (blockType == BIG_WIG_TYPE_GRAPH) {
                                for (var i = 0; i < itemCount; ++i) {
                                    var start = la[(i*3) + 6] + 1;
                                    var end   = la[(i*3) + 7];
                                    var score = fa[(i*3) + 8];
                                    if (start > end) {
                                        start = end;
                                    }
                                    maybeCreateFeature(start, end, {score: score});
                                }
                            } else {
                                dlog('Currently not handling bwgType=' + blockType);
                            }
                        } else if (thisB.bwg.type == 'bigbed') {
                            var offset = 0;
                            while (offset < ba.length) {
                                var chromId = (ba[offset+3]<<24) | (ba[offset+2]<<16) | (ba[offset+1]<<8) | (ba[offset+0]);
                                var start = (ba[offset+7]<<24) | (ba[offset+6]<<16) | (ba[offset+5]<<8) | (ba[offset+4]);
                                var end = (ba[offset+11]<<24) | (ba[offset+10]<<16) | (ba[offset+9]<<8) | (ba[offset+8]);
                                offset += 12;
                                var rest = '';
                                while (true) {
                                    var ch = ba[offset++];
                                    if (ch != 0) {
                                        rest += String.fromCharCode(ch);
                                    } else {
                                        break;
                                    }
                                }

                                var featureOpts = {};
                                
                                var bedColumns = rest.split('\t');
                                if (bedColumns.length > 0) {
                                    featureOpts.label = bedColumns[0];
                                }
                                if (bedColumns.length > 1) {
                                    featureOpts.score = 100; /* bedColumns[1]; */
                                }
                                if (bedColumns.length > 2) {
                                    featureOpts.orientation = bedColumns[2];
                                }

                                maybeCreateFeature(chromId, start + 1, end, featureOpts);
                            }
                        } else {
                            dlog("Don't know what to do with " + thisB.bwg.type);
                        }
                        blocksToFetch.splice(0, 1);
                        tramp();
                    } else {
                        var fetchStart = block.offset;
                        var fetchSize = block.size;
                        var bi = 1;
                        while (bi < blocksToFetch.length && blocksToFetch[bi].offset == (fetchStart + fetchSize)) {
                            fetchSize += blocksToFetch[bi].size;
                            ++bi;
                        }

                        thisB.bwg.data.slice(fetchStart, fetchSize).fetch(function(result) {
                            var offset = 0;
                            var bi = 0;
                            while (offset < fetchSize) {
                                var fb = blocksToFetch[bi];
                            
                                var data;
                                if (thisB.bwg.uncompressBufSize > 0) {
                                    // var beforeInf = Date.now()
                                    data = jszlib_inflate_buffer(result, offset + 2, fb.size - 2);
                                    // var afterInf = Date.now();
                                    // dlog('inflate: ' + (afterInf - beforeInf) + 'ms');
                                } else {
                                    var tmp = new Uint8Array(fb.size);    // FIXME is this really the best we can do?
                                    arrayCopy(new Uint8Array(result, offset, fb.size), 0, tmp, 0, fb.size);
                                    data = tmp.buffer;
                                }
                                fb.data = data;
                                
                                offset += fb.size;
                                ++bi;
                            }
                            tramp();
                        });
                    }
                }
            }
            tramp();
        }
    }

    cirFobRecur([thisB.cirTreeOffset + 48], 1);
}

//
// end cut/paste
//






BigWig.prototype.readWigData = function(chrName, min, max, callback) {
    this.getUnzoomedView().readWigData(chrName, min, max, callback);
}

BigWig.prototype.getUnzoomedView = function() {
    if (!this.unzoomedView) {
        var cirLen = 4000;
        var nzl = this.zoomLevels[0];
        if (nzl) {
            cirLen = this.zoomLevels[0].dataOffset - this.unzoomedIndexOffset;
        }
        this.unzoomedView = new BigWigView(this, this.unzoomedIndexOffset, cirLen, false);
    }
    return this.unzoomedView;
}

BigWig.prototype.getZoomedView = function(z) {
    var zh = this.zoomLevels[z];
    if (!zh.view) {
        zh.view = new BigWigView(this, zh.indexOffset, this.zoomLevels[z + 1].dataOffset - zh.indexOffset, true);
    }
    return zh.view;
}


function makeBwgFromURL(url, callback, creds) {
    makeBwg(new URLFetchable(url, {credentials: creds}), callback, url);
}

function makeBwgFromFile(file, callback) {
    makeBwg(new BlobFetchable(file), callback, 'file');
}

function makeBwg(data, callback, name) {
    var bwg = new BigWig();
    bwg.data = data;
    bwg.name = name;
    bwg.data.slice(0, 512).fetch(function(result) {
        if (!result) {
            return callback(null, "Couldn't fetch file");
        }

        var header = result;
        var sa = new Int16Array(header);
        var la = new Int32Array(header);
        if (la[0] == BIG_WIG_MAGIC) {
            bwg.type = 'bigwig';
        } else if (la[0] == BIG_BED_MAGIC) {
            bwg.type = 'bigbed';
        } else {
            callback(null, "Not a supported format");
        }
//        dlog('magic okay');

        bwg.version = sa[2];             // 4
        bwg.numZoomLevels = sa[3];       // 6
        bwg.chromTreeOffset = (la[2] << 32) | (la[3]);     // 8
        bwg.unzoomedDataOffset = (la[4] << 32) | (la[5]);  // 16
        bwg.unzoomedIndexOffset = (la[6] << 32) | (la[7]); // 24
        bwg.fieldCount = sa[16];         // 32
        bwg.definedFieldCount = sa[17];  // 34
        bwg.asOffset = (la[9] << 32) | (la[10]);    // 36 (unaligned longlong)
        bwg.totalSummaryOffset = (la[11] << 32) | (la[12]);    // 44 (unaligned longlong)
        bwg.uncompressBufSize = la[13];  // 52
         
        // dlog('bigType: ' + bwg.type);
        // dlog('chromTree at: ' + bwg.chromTreeOffset);
        // dlog('uncompress: ' + bwg.uncompressBufSize);
        // dlog('data at: ' + bwg.unzoomedDataOffset);
        // dlog('index at: ' + bwg.unzoomedIndexOffset);
        // dlog('field count: ' + bwg.fieldCount);
        // dlog('defined count: ' + bwg.definedFieldCount);

        bwg.zoomLevels = [];
        for (var zl = 0; zl < bwg.numZoomLevels; ++zl) {
            var zlReduction = la[zl*6 + 16]
            var zlData = (la[zl*6 + 18]<<32)|(la[zl*6 + 19]);
            var zlIndex = (la[zl*6 + 20]<<32)|(la[zl*6 + 21]);
//          dlog('zoom(' + zl + '): reduction=' + zlReduction + '; data=' + zlData + '; index=' + zlIndex);
            bwg.zoomLevels.push({reduction: zlReduction, dataOffset: zlData, indexOffset: zlIndex});
        }

        bwg.readChromTree(function() {
            return callback(bwg);
        });
    });
}
/* -*- mode: javascript; c-basic-offset: 4; indent-tabs-mode: nil -*- */

// 
// Dalliance Genome Explorer
// (c) Thomas Down 2006-2011
//
// bin.js general binary data support
//

function BlobFetchable(b) {
    this.blob = b;
}

BlobFetchable.prototype.slice = function(start, length) {
    var b;
    if (length) {
        b = this.blob.webkitSlice(start, start + length);
    } else {
        b = this.blob.webkitSlice(start);
    }
    return new BlobFetchable(b);
}

BlobFetchable.prototype.fetch = function(callback) {
    var reader = new FileReader();
    reader.onloadend = function(ev) {
        callback(bstringToBuffer(reader.result));
    };
    reader.readAsBinaryString(this.blob);
}

function URLFetchable(url, start, end, opts) {
    if (!opts) {
        if (typeof start === 'object') {
            opts = start;
            start = undefined;
        } else {
            opts = {};
        }
    }

    this.url = url;
    this.start = start || 0;
    if (end) {
        this.end = end;
    }
    this.opts = opts;
}

URLFetchable.prototype.slice = function(s, l) {
    var ns = this.start, ne = this.end;
    if (ns && s) {
        ns = ns + s;
    } else {
        ns = s || ns;
    }
    if (l && ns) {
        ne = ns + l - 1;
    } else {
        ne = ne || l - 1;
    }
    return new URLFetchable(this.url, ns, ne, this.opts);
}

URLFetchable.prototype.fetch = function(callback, attempt, truncatedLength) {
    var thisB = this;

    attempt = attempt || 1;
    if (attempt > 3) {
        return callback(null);
    }

    var req = new XMLHttpRequest();
    var length;
    req.open('GET', this.url, true);
    req.overrideMimeType('text/plain; charset=x-user-defined');
    if (this.end) {
        req.setRequestHeader('Range', 'bytes=' + this.start + '-' + this.end);
        length = this.end - this.start + 1;
    }
    req.responseType = 'arraybuffer';
    req.onreadystatechange = function() {
        if (req.readyState == 4) {
            if (req.status == 200 || req.status == 206) {
                if (req.response) {
                    return callback(req.response);
                } else if (req.mozResponseArrayBuffer) {
                    return callback(req.mozResponseArrayBuffer);
                } else {
                    var r = req.responseText;
                    if (length && length != r.length && (!truncatedLength || r.length != truncatedLength)) {
                        return thisB.fetch(callback, attempt + 1, r.length);
                    } else {
                        return callback(bstringToBuffer(req.responseText));
                    }
                }
            } else {
                return thisB.fetch(callback, attempt + 1);
            }
        }
    };
    if (this.opts.credentials) {
        req.withCredentials = true;
    }
    req.send('');
}

function bstringToBuffer(result) {
    if (!result) {
        return null;
    }

//    var before = Date.now();
    var ba = new Uint8Array(result.length);
    for (var i = 0; i < ba.length; ++i) {
        ba[i] = result.charCodeAt(i);
    }
//    var after  = Date.now();
//    dlog('bb took ' + (after - before) + 'ms');
    return ba.buffer;
}

/* -*- mode: javascript; c-basic-offset: 4; indent-tabs-mode: nil -*- */

// 
// Dalliance Genome Explorer
// (c) Thomas Down 2006-2010
//
// browser.js: browser setup and UI.
//

// constants

var NS_SVG = 'http://www.w3.org/2000/svg';
var NS_HTML = 'http://www.w3.org/1999/xhtml';
var NS_XLINK = 'http://www.w3.org/1999/xlink';

// Limit stops

MAX_VIEW_SIZE=500000;

function Browser(opts) {
    if (!opts) {
        opts = {};
    }

    this.sources = [];
    this.tiers = [];

    this.cookieKey = 'browser';
    this.karyoEndpoint = new DASSource('http://www.derkholm.net:8080/das/hsa_54_36p/');
    this.registry = 'http://www.dasregistry.org/das/sources';
    this.coordSystem = {
        speciesName: 'Human',
        taxon: 9606,
        auth: 'NCBI',
        version: '36'
    };
    this.chains = {};

    this.exportServer = 'http://www.biodalliance.org:8765/'

    this.pageName = 'svgHolder'
    this.maxExtra = 1.5;
    this.minExtra = 0.2;
    this.zoomFactor = 1.0;
    this.origin = 0;
    this.targetQuantRes = 5.0;
    this.featurePanelWidth = 750;
    this.zoomBase = 100;
    this.zoomExpt = 30; // Now gets clobbered.
    this.entryPoints = null;
    this.currentSeqMax = -1; // init once EPs are fetched.

    this.highlight = false;
    this.highlightMin = -1
    this.highlightMax = - 1;

    this.autoSizeTiers = false;
    this.guidelineStyle = 'foreground';
    this.guidelineSpacing = 75;
    this.fgGuide = null;
    this.positionFeedback = false;

    this.selectedTier = 1;

    this.placards = [];

    // Visual config.

    this.tierBackgroundColors = ["rgb(245,245,245)", "rgb(230,230,250)"];
    this.minTierHeight = 25;
    
    this.tabMargin = 120;

    this.browserLinks = {
        Ensembl: 'http://ncbi36.ensembl.org/Homo_sapiens/Location/View?r=${chr}:${start}-${end}',
        UCSC: 'http://genome.ucsc.edu/cgi-bin/hgTracks?db=hg18&position=chr${chr}:${start}-${end}'
    }

    this.iconsURI = 'http://www.biodalliance.org/resources/icons.svg'

    // Registry

    this.availableSources = new Observed();
    this.defaultSources = [];
    this.mappableSources = {};

    for (var k in opts) {
        this[k] = opts[k];
    }

    var thisB = this;
    window.addEventListener('load', function(ev) {thisB.realInit();}, false);
}


function formatQuantLabel(v) {
    var t = '' + v;
    var dot = t.indexOf('.');
    if (dot < 0) {
        return t;
    } else {
        var dotThreshold = 2;
        if (t.substring(0, 1) == '-') {
            ++dotThreshold;
        }

        if (dot >= dotThreshold) {
            return t.substring(0, dot);
        } else {
            return t.substring(0, dot + 2);
        }
    }
}

Browser.prototype.labelForTier = function(tier, ti, labelGroup) {
    var labelWidth = this.tabMargin;
    var viewportBackground = document.createElementNS(NS_SVG, 'path');
    viewportBackground.setAttribute('d', 'M 15 ' + 2 + 
                                    ' L 10 ' + 7 +
                                    ' L 10 ' + 18 +
                                    ' L 15 ' + 22 +
                                    ' L ' + (10 + labelWidth) + ' ' + 22 +
                                    ' L ' + (10 + labelWidth) + ' ' + 2 + ' Z');
    var fill = this.tierBackgroundColors[ti % this.tierBackgroundColors.length];
    if (ti == this.selectedTier) {
        fill = 'rgb(240, 200, 200)';
    }
    //     dlog('tier ' + ti + '; fill=' + fill + '; sel= ' + tier.selectedTier);
    viewportBackground.setAttribute('fill', fill);
    viewportBackground.setAttribute('stroke', 'none');
    labelGroup.appendChild(viewportBackground);
    this.setupTierDrag(viewportBackground, ti);

    var hasWidget = false;
    if (tier.dasSource.collapseSuperGroups || tier.hasBumpedFeatures) {
        hasWidget = true;
        this.makeToggleButton(labelGroup, tier, 0);
    } 

    if (tier.isQuantitative) {
        hasWidget = true;
        var quantTools = makeElementNS(NS_SVG, 'g');
        quantTools.appendChild(makeElementNS(NS_SVG, 'rect', null, {
            x: this.tabMargin - 25,
            y: 0,
            width: 25,
            height: tier.layoutHeight,
            stroke: 'none',
            fill: this.tierBackgroundColors[ti % this.tierBackgroundColors.length]
        }));
        labelGroup.appendChild(quantTools);
        quantTools.appendChild(makeElementNS(NS_SVG, 'line', null, {
            x1: this.tabMargin,
            y1: 0 + (tier.clientMin|0),
            x2: this.tabMargin,
            y2: 0 + (tier.clientMax|0),
            strokeWidth: 1
        }));
        quantTools.appendChild(makeElementNS(NS_SVG, 'line', null, {
            x1: this.tabMargin -5 ,
            y1: 0 + (tier.clientMin|0),
            x2: this.tabMargin,
            y2: 0 + (tier.clientMin|0),
            strokeWidth: 1
        }));
        quantTools.appendChild(makeElementNS(NS_SVG, 'line', null, {
            x1: this.tabMargin -3 ,
            y1: 0 + ((tier.clientMin|0) +(tier.clientMax|0))/2 ,
            x2: this.tabMargin,
            y2: 0 + ((tier.clientMin|0) +(tier.clientMax|0))/2,
            strokeWidth: 1
        }));
        quantTools.appendChild(makeElementNS(NS_SVG, 'line', null, {
            x1: this.tabMargin -5 ,
            y1: 0 + (tier.clientMax|0),
            x2: this.tabMargin,
            y2: 0 + (tier.clientMax|0),
            strokeWidth: 1
        }));
        var minQ = makeElementNS(NS_SVG, 'text', formatQuantLabel(tier.min), {
            x: 80,
            y:  (tier.clientMin|0),
            strokeWidth: 0,
            fill: 'black',
            fontSize: '8pt'
        });
        quantTools.appendChild(minQ);
        var mqbb = minQ.getBBox();
        minQ.setAttribute('x', this.tabMargin - mqbb.width - 7);
        minQ.setAttribute('y', (tier.clientMin|0) + (mqbb.height/2) - 4);
                    
        var maxQ = makeElementNS(NS_SVG, 'text', formatQuantLabel(tier.max), {
            x: 80,
            y: (tier.clientMax|0),
            strokeWidth: 0,
            fill: 'black',
            fontSize: '8pt'
        });
        quantTools.appendChild(maxQ);
        maxQ.setAttribute('x', this.tabMargin - maxQ.getBBox().width - 3);
        mqbb = maxQ.getBBox();
        maxQ.setAttribute('x', this.tabMargin - mqbb.width - 7);
        maxQ.setAttribute('y', (tier.clientMax|0) + (mqbb.height/2) -1 );
        
        var button = this.icons.createIcon('magnifier', labelGroup);
        button.setAttribute('transform', 'translate(' + (this.tabMargin - 18) + ', ' + ((tier.layoutHeight/2) - 8) + '), scale(0.6,0.6)');
        
        // FIXME style-changes don't currently work because of the way icons get grouped.
        button.addEventListener('mouseover', function(ev) {
            button.setAttribute('fill', 'red');
        }, false);
        button.addEventListener('mouseout', function(ev) {
            button.setAttribute('stroke', 'gray');
        }, false);
                
        quantTools.appendChild(button);
        this.makeQuantConfigButton(quantTools, tier, 0);
        this.makeTooltip(quantTools, 'Click to adjust how this data is displayed');
    }

    var labelMaxWidth = this.tabMargin - 20;
    if (hasWidget) {
        labelMaxWidth -= 20;
    }
    var labelString = tier.dasSource.name;
    var labelText = document.createElementNS(NS_SVG, 'text');
    labelText.setAttribute('x', 15);
    labelText.setAttribute('y', 17);
    labelText.setAttribute('stroke-width', 0);
    labelText.setAttribute('fill', 'black');
    labelText.appendChild(document.createTextNode(labelString));
    labelText.setAttribute('pointer-events', 'none');
    labelGroup.appendChild(labelText);

    try {
        while (labelText.getBBox().width > labelMaxWidth) {
            removeChildren(labelText);
            labelString = labelString.substring(0, labelString.length - 1);
            labelText.appendChild(document.createTextNode(labelString + '...'));
        }
    } catch (e) {/* probably means that the BBox isn't available yet due to hidden components */}
    return labelGroup;
}

Browser.prototype.arrangeTiers = function() {
    var browserSvg = this.svgRoot;
    for (var p = 0; p < this.placards.length; ++p) {
        browserSvg.removeChild(this.placards[p]);
    }
    this.placards = [];

    var labelGroup = this.dasLabelHolder;
        
    var clh = 50;
    for (ti = 0; ti < this.tiers.length; ++ti) {
        var tier = this.tiers[ti];
        tier.y = clh;
        
        if (!tier.isLabelValid) {
            if (tier.label) {
                labelGroup.removeChild(tier.label);
            }
            tier.label = makeElementNS(NS_SVG, 'g');
            labelGroup.appendChild(tier.label);
            this.labelForTier(tier, ti, tier.label);
        }

        this.xfrmTier(tier, this.tabMargin - ((1.0 * (this.viewStart - this.origin)) * this.scale), -1);
            
        if (tier.placard) {
            tier.placard.setAttribute('transform', 'translate(' + this.tabMargin + ', ' + (clh + tier.layoutHeight - 4) + ')');
            browserSvg.appendChild(tier.placard);
            this.placards.push(tier.placard);
        }

        clh += tier.layoutHeight;
    }
        
    this.featureBackground.setAttribute('height', ((clh | 0) - 50));

    if (clh < 150) {
        clh = 150;
    }
        
    if (this.browserFrameHeight != clh) {
        this.svgRoot.setAttribute("height", "" + ((clh | 0) + 10) + "px");
        this.svgBackground.setAttribute("height", "" + ((clh | 0) + 10));
        this.featureClipRect.setAttribute("height", "" + ((clh | 0) - 10));
        this.labelClipRect.setAttribute("height", "" + ((clh | 0) - 10));
        this.browserFrameHeight = clh;
    }
}

Browser.prototype.offsetForTier = function(ti) {
    var clh = 50;
    for (var t = 0; t < ti; ++t) {
        clh += this.tiers[t].layoutHeight;
    }
    return clh;
}

Browser.prototype.tierInfoPopup = function(tier, ev) {
    var regel;

    var popcontents = [];
    if (tier.dasSource.desc) {
        popcontents.push(tier.dasSource.desc);
    }

    var srcs = this.availableSources.get();
    if (tier.dasSource.mapping) {
        var mcs = this.chains[tier.dasSource.mapping].coords;
        popcontents.push(makeElement('p', makeElement('i', 'Mapped from ' + mcs.auth + mcs.version)));
        srcs = this.mappableSources[tier.dasSource.mapping].get();
    }

    if (!srcs || srcs == 0) {
        regel = makeElement('p', 'Registry data not available');
    } else {
        for (var ri = 0; ri < srcs.length; ++ri) {
            var re = srcs[ri];
            if (re.uri == tier.dasSource.uri && re.source_uri) {
                regel = makeElement('p', makeElement('a', 'Registry entry: ' + re.name, {href: 'http://www.dasregistry.org/showdetails.jsp?auto_id=' + re.source_uri, target: '_new'})); 
                break;
            }
        }
        if (!regel) {
            regel = makeElement('p', 'No registry information for this source');
        }
    }

    popcontents.push(regel);

    this.popit(ev, tier.dasSource.name, popcontents, {width: 300});
}

Browser.prototype.setupTierDrag = function(element, ti) {
    var thisB = this;
    var dragOriginX, dragOriginY;
    var dragFeedbackRect;
    var targetTier;
    var clickTimeout = null;
    var tier = this.tiers[ti];
    
    var moveHandler = function(ev) {
        var cly = ((ev.clientY + window.scrollY - dragOriginY) | 0) - 50;
        var destTier = 0;
        while (destTier < thisB.tiers.length && cly > thisB.tiers[destTier].layoutHeight) {
            cly -= thisB.tiers[destTier].layoutHeight;
            ++destTier;
        }
        if (destTier != targetTier) {
            targetTier = destTier;
            dragFeedbackRect.setAttribute('y', thisB.offsetForTier(targetTier) - 2);
        }
    };
    
    var binned = false;
    var binEnterHandler = function(ev) {
        thisB.bin.setAttribute('stroke', 'red');
        dragFeedbackRect.setAttribute('fill', 'none');
        binned = true;
    }
    var binLeaveHandler = function(ev) {
        thisB.bin.setAttribute('stroke', 'gray');
        dragFeedbackRect.setAttribute('fill', 'red');
        binned = false;
    }
    
    var upHandler = function(ev) {
        window.removeEventListener('mousemove', moveHandler, true);
        window.removeEventListener('mouseup', upHandler, true);
        thisB.bin.removeEventListener('mouseover', binEnterHandler, true);
        thisB.bin.removeEventListener('mouseout', binLeaveHandler, true);
        thisB.bin.setAttribute('stroke', 'gray');

        if (clickTimeout) {
            clearTimeout(clickTimeout);
            clickTimeout = null;
            thisB.tierInfoPopup(tier, ev);
            return;
        }

        thisB.popupHolder.removeChild(dragFeedbackRect);
        if (binned) {
            thisB.removeTier(thisB.tiers[ti]);
        } else if (targetTier == ti) {
            // Nothing at all.
        } else {
            var newTiers = [];
            
            var fromCnt = 0;
            if (targetTier > ti) {
                --targetTier;
            }
            while (newTiers.length < thisB.tiers.length) {
                if (newTiers.length == targetTier) {
                    newTiers.push(thisB.tiers[ti]);
                } else {
                    if (fromCnt != ti) {
                        newTiers.push(thisB.tiers[fromCnt]);
                    }
                    ++fromCnt;
                }
            }
            
            thisB.tiers = newTiers;
            if (thisB.knownSpace) {
                thisB.knownSpace.tierMap = thisB.tiers;
            }
            for (var nti = 0; nti < thisB.tiers.length; ++nti) {
                thisB.tiers[nti].background.setAttribute("fill", thisB.tierBackgroundColors[nti % thisB.tierBackgroundColors.length]);
                thisB.tiers[nti].isLabelValid = false;
            }
            
            thisB.arrangeTiers();
            thisB.storeStatus();
        }
    }
    
    element.addEventListener('mousedown', function(ev) {
        thisB.removeAllPopups();
        ev.stopPropagation(); ev.preventDefault();
        
        var origin = thisB.svgHolder.getBoundingClientRect();
        dragOriginX = origin.left + window.scrollX; dragOriginY = origin.top + window.scrollY;
        window.addEventListener('mousemove', moveHandler, true);
        window.addEventListener('mouseup', upHandler, true);
        thisB.bin.addEventListener('mouseover', binEnterHandler, true);
        thisB.bin.addEventListener('mouseout', binLeaveHandler, true);
        targetTier = ti;
        dragFeedbackRect = makeElementNS(NS_SVG, 'rect', null, {
            x: thisB.tabMargin,
            y: thisB.offsetForTier(targetTier) - 2,
            width: thisB.featurePanelWidth,
            height: 4,
            fill: 'red',
            stroke: 'none'
        });
        
        clickTimeout = setTimeout(function() {
            clickTimeout = null;
            // We can do all the setup on click, but don't show the feedback rectangle
            // until we're sure it's a click rather than a drag.
            thisB.popupHolder.appendChild(dragFeedbackRect);
        }, 200);

    },true);
}

Browser.prototype.makeToggleButton = function(labelGroup, tier, ypos) {
    var thisB = this;
    var bumpToggle = makeElementNS(NS_SVG, 'g', null, {fill: 'cornsilk', strokeWidth: 1, stroke: 'gray'});
    bumpToggle.appendChild(makeElementNS(NS_SVG, 'rect', null, {x: this.tabMargin - 15, y: ypos + 8, width: 8, height: 8}));
    bumpToggle.appendChild(makeElementNS(NS_SVG, 'line', null, {x1: this.tabMargin - 15, y1: ypos + 12, x2: this.tabMargin - 7, y2: ypos+12}));
    if (!tier.bumped) {
        bumpToggle.appendChild(makeElementNS(NS_SVG, 'line', null, {x1: this.tabMargin - 11, y1: ypos+8, x2: this.tabMargin - 11, y2: ypos+16}));
    }
    labelGroup.appendChild(bumpToggle);
    bumpToggle.addEventListener('mouseover', function(ev) {bumpToggle.setAttribute('stroke', 'red');}, false);
    bumpToggle.addEventListener('mouseout', function(ev) {
        bumpToggle.setAttribute('stroke', 'gray');
    }, false);
    bumpToggle.addEventListener('mousedown', function(ev) {
        tier.bumped = !tier.bumped;
        tier.layoutWasDone = false;   // permits the feature-tier layout code to resize the tier.
        tier.isLabelValid = false;
        tier.draw();
    }, false);
    this.makeTooltip(bumpToggle, 'Click to ' + (tier.bumped ? 'collapse' : 'expand'));
}

Browser.prototype.updateRegion = function() {
    if (this.updateRegionBaton) {
        // dlog('UR already pending');
    } else {
        var thisB = this;
        this.updateRegionBaton = setTimeout(function() {
            thisB.updateRegionBaton = null;
            thisB.realUpdateRegion();
        }, 25);
    }
}

Browser.prototype.realUpdateRegion = function()
{
    var chrLabel = this.chr;
    if (chrLabel.indexOf('chr') < 0) {
        chrLabel = 'chr' + chrLabel;
    }
    var fullLabel = chrLabel + ':' + (this.viewStart|0) + '..' + (this.viewEnd|0);

    removeChildren(this.regionLabel);
    this.regionLabel.appendChild(document.createTextNode(fullLabel));
    var bb = NULL_BBOX;
    try { 
        this.regionLabel.getBBox();
    } catch (e) {};
    var rlm = bb.x + bb.width;
    if (this.regionLabelMax && rlm > this.regionLabelMax) {
        removeChildren(this.regionLabel);
        this.regionLabel.appendChild(document.createTextNode(chrLabel));
    }
}

Browser.prototype.refresh = function() {
    var width = (this.viewEnd - this.viewStart) + 1;
    var minExtraW = (width * this.minExtra) | 0;
    var maxExtraW = (width * this.maxExtra) | 0;

    
    var newOrigin = (this.viewStart + this.viewEnd) / 2;
    var oh = newOrigin - this.origin;
    this.origin = newOrigin;
    this.scaleAtLastRedraw = this.scale;
    for (var t = 0; t < this.tiers.length; ++t) {
        var od = oh;
        if (this.tiers[t].originHaxx) {
            od += this.tiers[t].originHaxx;
        }
        this.tiers[t].originHaxx = od;
    }

    var scaledQuantRes = this.targetQuantRes / this.scale;


    var innerDrawnStart = Math.max(1, (this.viewStart|0) - minExtraW);
    var innerDrawnEnd = Math.min((this.viewEnd|0) + minExtraW, ((this.currentSeqMax|0) > 0 ? (this.currentSeqMax|0) : 1000000000))
    var outerDrawnStart = Math.max(1, (this.viewStart|0) - maxExtraW);
    var outerDrawnEnd = Math.min((this.viewEnd|0) + maxExtraW, ((this.currentSeqMax|0) > 0 ? (this.currentSeqMax|0) : 1000000000));

    if (!this.knownSpace || this.knownSpace.chr !== this.chr) {
        var ss = null;
        for (var i = 0; i < this.tiers.length; ++i) {
            if (this.tiers[i].sequenceSource) {
                ss = this.tiers[i].sequenceSource;
                break;
            }
        }
        this.knownSpace = new KnownSpace(this.tiers, this.chr, outerDrawnStart, outerDrawnEnd, scaledQuantRes, ss);
    }
    
    var seg = this.knownSpace.bestCacheOverlapping(this.chr, innerDrawnStart, innerDrawnEnd);
    if (seg && seg.min <= innerDrawnStart && seg.max >= innerDrawnEnd) {
        this.drawnStart = Math.max(seg.min, outerDrawnStart);
        this.drawnEnd = Math.min(seg.max, outerDrawnEnd);
    } else {
        this.drawnStart = outerDrawnStart;
        this.drawnEnd = outerDrawnEnd;
    }

    this.knownSpace.viewFeatures(this.chr, this.drawnStart, this.drawnEnd, scaledQuantRes);
}


// var originX;
// var dcTimeoutID = null;
// var clickTestTB = null;

Browser.prototype.mouseDownHandler = function(ev)
{
    var thisB = this;
    this.removeAllPopups();
    ev.stopPropagation(); ev.preventDefault();

    var target = document.elementFromPoint(ev.clientX, ev.clientY);
    while (target && !target.dalliance_feature && !target.dalliance_group) {
        target = target.parentNode;
    }

    if (target && (target.dalliance_feature || target.dalliance_group)) {
        if (this.dcTimeoutID && target.dalliance_feature) {
            var f = target.dalliance_feature;
            var org = this.svgHolder.getBoundingClientRect();
            var fstart = (((f.min|0) - (this.viewStart|0)) * this.scale) + org.left + this.tabMargin;
            var fwidth = (((f.max - f.min) + 1) * this.scale);

            clearTimeout(this.dcTimeoutID);
            this.dcTimeoutID = null;

            var newMid = (((target.dalliance_feature.min|0) + (target.dalliance_feature.max|0)))/2;
            if (fwidth > 10) {
                var frac = (1.0 * (ev.clientX - fstart)) / fwidth;
                if (frac < 0.3) {
                    newMid = (target.dalliance_feature.min|0);
                } else  if (frac > 0.7) {
                    newMid = (target.dalliance_feature.max|0) + 1;
                }
            }

            var width = this.viewEnd - this.viewStart;
            this.setLocation(newMid - (width/2), newMid + (width/2));
            
            var extraPix = this.featurePanelWidth - ((width+1)*this.scale);
            // alert(extraPix);
            if (Math.abs(extraPix) > 1) {
                this.move(extraPix/2);
            }
        } else {
            this.dcTimeoutID = setTimeout(function() {
                thisB.dcTimeoutID = null;
                thisB.featurePopup(ev, target.dalliance_feature, target.dalliance_group);
            }, 200);
        }
    } else {
        this.originX = ev.clientX;
        document.addEventListener('mousemove', this.__mouseMoveHandler, true);
        document.addEventListener('mouseup', this.__mouseUpHandler, true);
        this.clickTestTB = setTimeout(function() {
            thisB.clickTestTB = null;
        }, 200);
    }
}


var TAGVAL_NOTE_RE = new RegExp('^([A-Za-z]+)=(.+)');

Browser.prototype.featurePopup = function(ev, feature, group){
    if (!feature) feature = {};
    if (!group) group = {};

    this.removeAllPopups();

    var table = makeElement('table', null);
    table.style.width = '100%';

    var name = pick(group.type, feature.type);
    //var fid = pick(group.label, feature.label, group.id, feature.id);
    //mpi2 edit here
    var fid = pick(feature.label,group.label, group.id, feature.id);
    
    if (fid && fid.indexOf('__dazzle') != 0) {
        name = name + ': ' + fid;
    }

    var idx = 0;
    if (feature.method) {
        var row = makeElement('tr', [
            makeElement('th', 'Method'),
            makeElement('td', feature.method)
        ]);
        row.style.backgroundColor = this.tierBackgroundColors[idx % this.tierBackgroundColors.length];
        table.appendChild(row);
        ++idx;
    }
    {
        var loc;
        if (group.segment) {
            loc = group;
        } else {
            loc = feature;
        }
        var row = makeElement('tr', [
            makeElement('th', 'Location'),
            makeElement('td', loc.segment + ':' + loc.min + '-' + loc.max)
        ]);
        row.style.backgroundColor = this.tierBackgroundColors[idx % this.tierBackgroundColors.length];
        table.appendChild(row);
        ++idx;
    }
    if (feature.score !== undefined && feature.score !== null && feature.score != '-') {
        var row = makeElement('tr', [
            makeElement('th', 'Score'),
            makeElement('td', '' + feature.score)
        ]);
        row.style.backgroundColor = this.tierBackgroundColors[idx % this.tierBackgroundColors.length];
        table.appendChild(row);
        ++idx;
    }
    {
        var links = maybeConcat(group.links, feature.links);
        if (links && links.length > 0) {
            var row = makeElement('tr', [
                makeElement('th', 'Links'),
                makeElement('td', links.map(function(l) {
                	//mpi2 edit here
                    //<img src="url" alt="some_text"/>
                if(l.desc=='Cassette Image'){
                   // console.debug(l.desc);
                return makeElement('div',makeElement('a', makeElement('img', l.desc, {width:320, src: l.uri}), {href:l.uri, target: '_new'}));//'<img src="http://www.knockoutmouse.org/targ_rep/alleles/37256/allele-image" alt="some_text"/>');
                }
                //if not image do something else here
                return makeElement('div', makeElement('a', l.desc, {href: l.uri, target: '_new'}));
                }))
            ]);
            row.style.backgroundColor = this.tierBackgroundColors[idx % this.tierBackgroundColors.length];
            table.appendChild(row);
            ++idx;
        }
    }
    {
        var notes = maybeConcat(group.notes, feature.notes);
        for (var ni = 0; ni < notes.length; ++ni) {
            var k = 'Note';
            var v = notes[ni];
            var m = v.match(TAGVAL_NOTE_RE);
            if (m) {
                k = m[1];
                v = m[2];
            }

            var row = makeElement('tr', [
                makeElement('th', k),
                makeElement('td', v)
            ]);
            row.style.backgroundColor = this.tierBackgroundColors[idx % this.tierBackgroundColors.length];
            table.appendChild(row);
            ++idx;
        }
    }

    this.popit(ev, name, table, {width: 400});
}

Browser.prototype.mouseUpHandler = function(ev) {
    var thisB = this;

    if (this.clickTestTB && this.positionFeedback) {
        var origin = svgHolder.getBoundingClientRect();
        var ppos = ev.clientX - origin.left - this.tabMargin;
        var spos = (((1.0*ppos)/this.scale) + this.viewStart)|0;
        
        var mx = ev.clientX + window.scrollX, my = ev.clientY + window.scrollY;
        var popup = makeElement('div', '' + spos, {}, {
            position: 'absolute',
            top: '' + (my + 20) + 'px',
            left: '' + Math.max(mx - 30, 20) + 'px',
            backgroundColor: 'rgb(250, 240, 220)',
            borderWidth: '1px',
            borderColor: 'black',
            borderStyle: 'solid',
            padding: '2px',
            maxWidth: '400px'
        });
        this.hPopupHolder.appendChild(popup);
        var moveHandler;
        moveHandler = function(ev) {
            try {
                thisB.hPopupHolder.removeChild(popup);
            } catch (e) {
                // May have been removed by other code which clears the popup layer.
            }
            window.removeEventListener('mousemove', moveHandler, false);
        }
        window.addEventListener('mousemove', moveHandler, false);
    }
    
    ev.stopPropagation(); ev.preventDefault();

    document.removeEventListener('mousemove', this.__mouseMoveHandler, true);
    document.removeEventListener('mouseup', this.__mouseUpHandler, true);
    this.storeStatus();
}

Browser.prototype.mouseMoveHandler = function(ev) {
    ev.stopPropagation(); ev.preventDefault();
    if (ev.clientX != this.originX) {
        this.move(ev.clientX - this.originX);
        this.originX = ev.clientX;
    }
}


Browser.prototype.touchStartHandler = function(ev)
{
    removeAllPopups();
    ev.stopPropagation(); ev.preventDefault();
    
    this.touchOriginX = ev.touches[0].pageX;
}

Browser.prototype.touchMoveHandler = function(ev)
{
    ev.stopPropagation(); ev.preventDefault();
    
    var touchX = ev.touches[0].pageX;
    // dlog('tl=' + ev.touches.length + 'tx=' + touchX + '; ox=' + this.touchOriginX);
    if (this.touchOriginX && touchX != this.touchOriginX) {
        this.move(touchX - this.touchOriginX);
    }
    this.touchOriginX = touchX;
}

Browser.prototype.touchEndHandler = function(ev)
{
    ev.stopPropagation(); ev.preventDefault();
    this.storeStatus();
}

Browser.prototype.touchCancelHandler = function(ev) {
}


Browser.prototype.removeAllPopups = function() {
    removeChildren(this.popupHolder);
    removeChildren(this.hPopupHolder);
}

function EPMenuItem(entryPoint) {
    this.entryPoint = entryPoint;
    this.nums = stringToNumbersArray(entryPoint.name);
}

Browser.prototype.makeHighlight = function() {
    if (this.highlight) {
        this.dasTierHolder.removeChild(this.highlight);
        this.highlight = null;
    }

    if (this.highlightMin > 0) {
        this.highlight = document.createElementNS(NS_SVG, 'rect');
        this.highlight.setAttribute('x', (this.highlightMin - this.origin) * this.scale);
        this.highlight.setAttribute('y', 0);
        this.highlight.setAttribute('width', (this.highlightMax - this.highlightMin + 1) * this.scale);
        this.highlight.setAttribute('height', 10000);
        this.highlight.setAttribute('stroke', 'none');
        this.highlight.setAttribute('fill', 'red');
        this.highlight.setAttribute('fill-opacity', 0.15);
        this.highlight.setAttribute('pointer-events', 'none');
        this.dasTierHolder.appendChild(this.highlight);
    }
}

Browser.prototype.init = function() {
    // Just here for backwards compatibility.
}

Browser.prototype.realInit = function(opts) {
    if (!opts) {
        opts = {};
    }
    this.supportsBinary = (typeof Int8Array === 'function');
    // dlog('supports binary: ' + this.supportsBinary);

    var thisB = this;
    // Cache away the default sources before anything else

    this.defaultSources = [];
    for (var i = 0; i < this.sources.length; ++i) {
        this.defaultSources.push(this.sources[i]);
    }
    this.defaultChr = this.chr;
    this.defaultStart = this.viewStart;
    this.defaultEnd = this.viewEnd;

    this.icons = new IconSet(this.iconsURI);

    var overrideSources;
    var reset = false;
    var qChr = null, qMin = null, qMax = null;
    
    //
    // Configuration processing
    //

    var queryDict = {};
    if (location.search) {
        var query = location.search.substring(1);
        var queries = query.split(new RegExp('[&;]'));
        for (var qi = 0; qi < queries.length; ++qi) {
            var kv = queries[qi].split('=');
            var k = decodeURIComponent(kv[0]), v=null;
            if (kv.length > 1) {
                v = decodeURIComponent(kv[1]);
            }
            queryDict[k] = v;
        }
        
        reset = queryDict.reset;
    }

    var storedConfigVersion = localStorage['dalliance.' + this.cookieKey + '.version'];
    if (storedConfigVersion) {
        storedConfigVersion = storedConfigVersion|0;
    } else {
        storedConfigVersion = -100;
    }
    if (VERSION.CONFIG != storedConfigVersion) {
//        dlog("Don't understand config version " + storedConfigVersion + ", resetting.");
        reset = true;
    }

    var storedConfigHash = localStorage['dalliance.' + this.cookieKey + '.configHash'] || '';
    var pageConfigHash = hex_sha1(miniJSONify(this.sources));   // okay to switch this to "real" JSON?
    if (pageConfigHash != storedConfigHash) {
//        alert('page config seems to have changed, resetting');
        reset=true;
        localStorage['dalliance.' + this.cookieKey + '.configHash'] = pageConfigHash;
    }

    if (this.cookieKey && localStorage['dalliance.' + this.cookieKey + '.view-chr'] && !reset) {
        qChr = localStorage['dalliance.' + this.cookieKey + '.view-chr'];
        qMin = localStorage['dalliance.' + this.cookieKey + '.view-start']|0;
        qMax = localStorage['dalliance.' + this.cookieKey + '.view-end']|0;
    }

    if (this.cookieKey) {
        var maybeSourceConfig = localStorage['dalliance.' + this.cookieKey + '.sources'];
        if (maybeSourceConfig && !reset) {
            overrideSources = JSON.parse(maybeSourceConfig);
        }
    }
    
    var region_exp = /([\d+,\w,\.,\_,\-]+):(\d+)[\-,\,](\d+)/;

    var queryRegion = false;
    if (queryDict.chr) {
        var qChr = queryDict.chr;
        var qMin = queryDict.min;
        var qMax = queryDict.max;
        queryRegion = true;
    }

    this.positionFeedback = queryDict.positionFeedback || false;
    guidelineConfig = queryDict.guidelines || 'foreground';
    if (guidelineConfig == 'true') {
        this.guidelineStyle = 'background';
    } else if (STRICT_NUM_REGEXP.test(guidelineConfig)) {
        this.guidelineStyle = 'background';
        this.guidelineSpacing = guidelineConfig|0;
    } else {
        this.guidelineStyle = guidelineConfig;
    }

    if (!queryRegion) {
        regstr = queryDict.r;
        if (!regstr) {
            regstr = queryDict.segment || '';
        }
        var match = regstr.match(region_exp);
        if ((regstr != '') && match) {
            qChr = match[1];
            qMin = match[2] | 0;
            qMax = match[3] | 0;
        }
        queryRegion = true;
    }
        
    if (qMax < qMin) {
        qMax = qMin + 10000;
    }

    var histr = queryDict.h || '';
    var match = histr.match(region_exp);
    if (match) {
        this.highlightMin = match[2]|0;
        this.highlightMax = match[3]|0;
    }

    //
    // Set up the UI (factor out?)
    //
           
    this.svgHolder = document.getElementById(this.pageName);
    this.svgRoot = makeElementNS(NS_SVG, 'svg', null, {
        version: '1.1',
        width: '860px',
        height: '500px',
        id: 'browser_svg'
    });
    removeChildren(this.svgHolder);
    this.svgHolder.appendChild(this.svgRoot);

    {
        var patdata = '';
         for (var i = -90; i <= 90; i += 20) {
             patdata = patdata + 'M ' + (Math.max(0, i) - 2) + ' ' + (Math.max(-i, 0) - 2) + ' L ' + (Math.min(100 + i, 100) + 2) + ' ' + (Math.min(100 - i, 100) + 2) + ' ';
             patdata = patdata + 'M ' + Math.max(i, 0) + ' ' + Math.min(i + 100, 100) + ' L ' + Math.min(i + 100, 100) + ' ' + Math.max(i, 0) + ' ';
        }
        var pat =  makeElementNS(NS_SVG, 'pattern',
                                 makeElementNS(NS_SVG, 'path', null, {
                                     stroke: 'lightgray',
                                     strokeWidth: 2,
                                     d: patdata
                                     // d: 'M 0 90 L 10 100 M 0 70 L 30 100 M 0 50 L 50 100 M 0 30 L 70 100 M 0 10 L 90 100 M 10 0 L 100 90 M 30 0 L 100 70 M 50 0 L 100 50 M 70 0 L 100 30 M 90 0 L 100 10'
                                     // 'M 0 90 L 90 0 M 0 70 L 70 0'
                                 }),
                                 {
                                     id: 'bgpattern-' + this.pageName,
                                     x: 0,
                                     y: 0,
                                     width: 100,
                                     height: 100
                                 });
        pat.setAttribute('patternUnits', 'userSpaceOnUse');
        this.svgRoot.appendChild(pat);
    }

    this.svgBackground = makeElementNS(NS_SVG, 'rect', null,  {id: 'background', fill: 'white' /*'url(#bgpattern-' + this.pageName + ')' */});
    var main = makeElementNS(NS_SVG, 'g', this.svgBackground, {
        fillOpacity: 1.0, 
        stroke: 'black', 
        strokeWidth: '0.1cm', 
        fontFamily: 'helvetica', 
        fontSize: '10pt'
    });
    this.svgRoot.appendChild(main);

    this.regionLabel = makeElementNS(NS_SVG, 'text', 'chr???', {
        x: 260,
        y: 30,
        strokeWidth: 0
    });
    main.appendChild(this.regionLabel);
    this.makeTooltip(this.regionLabel, 'Click to jump to a new location or gene');

    var addButton = this.icons.createButton('add-track', main, 30, 30);
    addButton.setAttribute('transform', 'translate(100, 10)');
    this.makeTooltip(addButton, 'Add tracks from the DAS registry');
    main.appendChild(addButton);

    var linkButton = this.icons.createButton('link', main, 30, 30);
    linkButton.setAttribute('transform', 'translate(140, 10)');
    this.makeTooltip(linkButton, 'Follow links to other genome browsers');
    main.appendChild(linkButton);

    var resetButton = this.icons.createButton('reset', main, 30, 30);
    resetButton.setAttribute('transform', 'translate(180, 10)');
    this.makeTooltip(resetButton, 'Reset the browser to a default state');
    main.appendChild(resetButton);

    var saveButton = this.icons.createButton('export', main, 30, 30);
    saveButton.setAttribute('transform', 'translate(220, 10)');
    this.makeTooltip(saveButton, 'Export the current genome display as a vector graphics file');
    main.appendChild(saveButton);
    var savePopupHandle;
    saveButton.addEventListener('mousedown', function(ev) {
        ev.stopPropagation(); ev.preventDefault();
        var showing = savePopupHandle && savePopupHandle.displayed;
        thisB.removeAllPopups();
        
        if (showing) {
            return;
        }

        var saveDoc = document.implementation.createDocument(NS_SVG, 'svg', null);
        var saveWidth = thisB.svgRoot.getAttribute('width')|0;
        saveDoc.documentElement.setAttribute('width', saveWidth);
        saveDoc.documentElement.setAttribute('height', thisB.svgRoot.getAttribute('height'));

        var saveRoot = makeElementNS(NS_SVG, 'g', null, {
            fontFamily: 'helvetica'
        });
        saveDoc.documentElement.appendChild(saveRoot);
        var dallianceAnchor = makeElementNS(NS_SVG, 'text', 'Graphics from Dalliance ' + VERSION, {
                x: 80,
                y: 30,
                strokeWidth: 0,
                fill: 'black',
                fontSize: '12pt'
        });
        thisB.svgRoot.appendChild(dallianceAnchor);
        var daWidth = dallianceAnchor.getBBox().width;
        thisB.svgRoot.removeChild(dallianceAnchor);
        dallianceAnchor.setAttribute('x', saveWidth - daWidth - 60);
        saveRoot.appendChild(dallianceAnchor);
        // dallianceAnchor.setAttributeNS(NS_XLINK, 'xlink:href', 'http://www.biodalliance.org/');
        
        var chrLabel = thisB.chr;
        if (chrLabel.indexOf('chr') < 0) {
            chrLabel = 'chr' + chrLabel;
        }
        var fullLabel = chrLabel + ':' + (thisB.viewStart|0) + '..' + (thisB.viewEnd|0);
        saveRoot.appendChild(makeElementNS(NS_SVG, 'text', fullLabel, {
            x: 40,
            y: 30,
            strokeWidth: 0,
            fill: 'black',
            fontSize: '12pt'
        })); 

        saveRoot.appendChild(labelClip.cloneNode(true));
        saveRoot.appendChild(thisB.dasLabelHolder.cloneNode(true));
        saveRoot.appendChild(featureClip.cloneNode(true));
        saveRoot.appendChild(thisB.dasTierHolder.cloneNode(true));

        var svgButton = makeElement('input', null, {
            type: 'radio',
            name: 'format',
            value: 'svg',
            checked: true
        });
        var pdfButton = makeElement('input', null, {
            type: 'radio',
            name: 'format',
            value: 'pdf'
        });
        var saveForm = makeElement('form', [makeElement('p', "To work around restrictions on saving files from web applications, image export currently requires transmission of the browser's current state to a remote server.  Depending on connection speed, this can take a few seconds -- please be patient."),
                                            makeElement('p', 'The download links only work once, so if you wish to keep or share your exported images, please save a copy on your computer'),
                                            svgButton, 'SVG', makeElement('br'),
                                            pdfButton, 'PDF', makeElement('br'),
                                            makeElement('br'),
                                            makeElement('input', null, {type: 'hidden',  name: 'svgdata', value: new XMLSerializer().serializeToString(saveDoc)}),
                                            makeElement('input', null, {type: 'submit'})],
                                   {action: thisB.exportServer + 'browser-image.svg', method: 'POST'});
        svgButton.addEventListener('click', function(cev) {
            saveForm.setAttribute('action', thisB.exportServer + 'browser-image.svg');
        }, false);
        pdfButton.addEventListener('click', function(cev) {
            saveForm.setAttribute('action', thisB.exportServer + 'browser-image.pdf');
        }, false);
        saveForm.addEventListener('submit', function(sev) {
            setTimeout(function() {
                thisB.removeAllPopups();
            }, 200);
            return true;
        }, false);
        savePopupHandle = thisB.popit(ev, 'Export', saveForm, {width: 400});
    }, false);

    this.bin = this.icons.createIcon('bin', main);
    this.bin.setAttribute('transform', 'translate(10, 18)');
    main.appendChild(this.bin);
    this.makeTooltip(this.bin, 'Drag tracks here to discard');
    
    this.featureClipRect = makeElementNS(NS_SVG, 'rect', null, {
        x: this.tabMargin,
        y: 50,
        width: 850 - this.tabMargin,
        height: 440
    });
    var featureClip = makeElementNS(NS_SVG, 'clipPath', this.featureClipRect, {id: 'featureClip-' + this.pageName});
    main.appendChild(featureClip);
    this.labelClipRect = makeElementNS(NS_SVG, 'rect', null, {
        x: 10,
        y: 50,
        width: this.tabMargin - 10,
        height: 440
    });
    var labelClip = makeElementNS(NS_SVG, 'clipPath', this.labelClipRect, {id: 'labelClip-' + this.pageName});
    main.appendChild(labelClip);
    
    this.featureBackground = makeElementNS(NS_SVG, 'rect', null, {
        x: this.tabMargin,
        y: 50,
        width: 850 - this.tabMargin,
        height: 440,
        stroke: 'none',
        fill: 'url(#bgpattern-' + this.pageName + ')'
    });
    main.appendChild(this.featureBackground);

    this.dasTierHolder = makeElementNS(NS_SVG, 'g', null, {clipPath: 'url(#featureClip-' + this.pageName + ')'});   // FIXME needs a unique ID.
    main.appendChild(this.dasTierHolder);
    var dasTiers = makeElementNS(NS_SVG, 'g', null, {id: 'dasTiers'});
    this.dasTierHolder.appendChild(dasTiers);

    this.makeHighlight();
    
    this.dasLabelHolder = makeElementNS(NS_SVG, 'g', makeElementNS(NS_SVG, 'g', null, {id: 'dasLabels'}), {clipPath: 'url(#labelClip-' + this.pageName + ')'}); 
    main.appendChild(this.dasLabelHolder);
    
    {
        var plusIcon = this.icons.createIcon('magnifier-plus', main);
        var minusIcon = this.icons.createIcon('magnifier-minus', main);
        this.zoomTickMarks = makeElementNS(NS_SVG, 'g');
        this.zoomSlider = new DSlider(250);
        this.zoomSlider.onchange = function(zoomVal, released) {
            thisB.zoom(Math.exp((1.0 * zoomVal) / thisB.zoomExpt));
            if (released) {
                thisB.invalidateLayouts();
                thisB.refresh();
                thisB.storeStatus();
            }
        };
        plusIcon.setAttribute('transform', 'translate(0,15)');
        plusIcon.setAttribute('pointer-events', 'all');
        plusIcon.addEventListener('mousedown', function(ev) {
            ev.stopPropagation(); ev.preventDefault();

            var oz = thisB.zoomSlider.getValue();
            thisB.zoomSlider.setValue(oz - 10);
            var nz = thisB.zoomSlider.getValue();
            if (nz != oz) {
                thisB.zoom(Math.exp((1.0 * nz) / thisB.zoomExpt));
                thisB.scheduleRefresh(500);
            }
        }, false);
        this.zoomSlider.svg.setAttribute('transform', 'translate(30, 0)');
        minusIcon.setAttribute('transform', 'translate(285,15)');
        minusIcon.setAttribute('pointer-events', 'all');
        minusIcon.addEventListener('mousedown', function(ev) {
            ev.stopPropagation(); ev.preventDefault();

            var oz = thisB.zoomSlider.getValue();
            thisB.zoomSlider.setValue(oz + 10);
            var nz = thisB.zoomSlider.getValue();
            if (nz != oz) {
                thisB.zoom(Math.exp((1.0 * nz) / thisB.zoomExpt));
                thisB.scheduleRefresh(500);
            }
        }, false);
        this.zoomWidget = makeElementNS(NS_SVG, 'g', [this.zoomTickMarks, plusIcon, this.zoomSlider.svg, minusIcon]);

        this.makeTooltip(this.zoomWidget, 'Drag to zoom');
        main.appendChild(this.zoomWidget);
    }

    this.karyo = new Karyoscape(this, this.karyoEndpoint);
    this.karyo.svg.setAttribute('transform', 'translate(480, 15)');
    this.karyo.onchange = function(pos) {
        var width = thisB.viewEnd - thisB.viewStart + 1;
        var newStart = ((pos * thisB.currentSeqMax) - (width/2))|0;
        var newEnd = newStart + width - 1;
        thisB.setLocation(newStart, newEnd);
    };
    main.appendChild(this.karyo.svg);
    
    this.popupHolder = makeElementNS(NS_SVG, 'g');
    main.appendChild(this.popupHolder);
    this.hPopupHolder = makeElement('div');
    this.hPopupHolder.style['font-family'] = 'helvetica';
    this.hPopupHolder.style['font-size'] = '12pt';
    this.svgHolder.appendChild(this.hPopupHolder);
  
    this.bhtmlRoot = makeElement('div');
    if (!this.disablePoweredBy) {
        this.bhtmlRoot.appendChild(makeElement('span', ['Powered by ', makeElement('a', 'Dalliance', {href: 'http://www.biodalliance.org/'}), ' ' + VERSION]));
    }
    this.svgHolder.appendChild(this.bhtmlRoot);
    
    if (this.guidelineStyle == 'foreground') {
        this.fgGuide = document.createElementNS(NS_SVG, 'line');
        this.fgGuide.setAttribute('x1', 500);
        this.fgGuide.setAttribute('y1', 50);
        this.fgGuide.setAttribute('x2', 500);
        this.fgGuide.setAttribute('y2', 10000);
        this.fgGuide.setAttribute('stroke', 'red');
        this.fgGuide.setAttribute('stroke-width', 1);
        this.fgGuide.setAttribute('pointer-events', 'none');
        main.appendChild(this.fgGuide);
    }
    
    // set up the linker

    var linkPopupHandle;
    linkButton.addEventListener('mousedown', function(ev) {
        var showing = linkPopupHandle && linkPopupHandle.displayed;
        ev.stopPropagation(); ev.preventDefault();
        thisB.removeAllPopups();
        if (showing) {
            return;
        }

        var linkList = makeElement('ul');
        for (l in thisB.browserLinks) {
            linkList.appendChild(makeElement('li', makeElement('a', l, {
                href: thisB.browserLinks[l].replace(new RegExp('\\${([a-z]+)}', 'g'), function(s, p1) {
                    if (p1 == 'chr') {
                        return thisB.chr;
                    } else if (p1 == 'start') {
                        return thisB.viewStart|0;
                    } else if (p1 == 'end') {
                        return thisB.viewEnd|0;
                    } else {
                        return '';
                    }
                }),
                target: '_new'
            })));
        }
        linkPopupHandle = thisB.popit(ev, 'Follow links to...', linkList);
    }, false);

    // set up the navigator

    var navPopupHandle;
    this.regionLabel.addEventListener('mousedown', function(ev) {
        ev.stopPropagation(); ev.preventDefault();
        var showing = navPopupHandle && navPopupHandle.displayed;
        thisB.removeAllPopups(); 
        if (showing) {
            return;
        }

        if (thisB.entryPoints == null) {
            alert("entry_points aren't currently available for this genome");
            return;
        }
        var epMenuItems = [], epsByChrName = {};
        for (var epi = 0; epi < thisB.entryPoints.length; ++epi) {
            epMenuItems.push(new EPMenuItem(thisB.entryPoints[epi]));
        }
        epMenuItems = epMenuItems.sort(function(epmi0, epmi1) {
            var n0 = epmi0.nums;
            var n1 = epmi1.nums;
            var idx = 0;
            while (true) {
                if (idx >= n0.length) {
                    return -1;
                } else if (idx >= n1.length) {
                    return 1;
                } else {
                    var dif = n0[idx] - n1[idx];
                    if (dif != 0) {
                        return dif;
                    } 
                }
                ++idx;
            }
        });

        var popup = makeElement('div');
        popup.style.padding = '5px';
        popup.style.paddingRight = '9px';
       
        {
            var form = makeElement('form');
            
            form.appendChild(document.createTextNode('Location:'));
            var locWarning = makeElement('div', null, {}, {'color': 'red'});
            form.appendChild(locWarning);
            var locInput = (makeElement('input', null, {type: 'text', value: (thisB.chr + ':' + (thisB.viewStart|0) + '..' + (thisB.viewEnd|0))}));
            form.appendChild(locInput);
            form.appendChild(makeElement('br'));
            form.appendChild(makeElement('input', null, {type: 'submit', value: 'Go'}));
            popup.appendChild(form);
        }
        navPopupHandle = thisB.popit(ev, 'Jump to...', popup, {width: 300});

        form.addEventListener('submit', function(ev) {
            ev.stopPropagation(); ev.preventDefault();

            var locString = locInput.value.trim();
            var match = /^([A-Za-z0-9]+)[:\t ]([0-9]+)([-:.\t ]+([0-9]+))?$/.exec(locString);
            if (match && match.length == 5) {
                var nchr = match[1];
	        var nmin = stringToInt(match[2]);
                if (match[4]) {
	            var nmax = stringToInt(match[4]);
                } else {
                    var wid = thisB.viewEnd - thisB.viewStart + 1;
                    nmin = nmin - (wid/2)|0;
                    nmax = nmin + wid;
                }
	        
                if (nchr != thisB.chr) {
                    thisB.highlightMin = -1;
                    thisB.highlightMax = -1;
                }
                
                try {
                    thisB.setLocation(nmin, nmax, nchr);
                    thisB.removeAllPopups();
                } catch (msg) {
                    removeChildren(locWarning);
                    locWarning.appendChild(document.createTextNode(msg));
                }
            } else {
                removeChildren(locWarning);
                locWarning.appendChild(document.createTextNode('Should match chr:start...end or chr:midpoint'));
            }
            return false;
        }, false);

        if (thisB.searchEndpoint) {
            var geneForm = makeElement('form');
            geneForm.appendChild(makeElement('p', 'Or search for...'))
            geneForm.appendChild(document.createTextNode('Gene:'));
            var geneInput = makeElement('input', null, {value: ''});
            geneForm.appendChild(geneInput);
            geneForm.appendChild(makeElement('br'));
            geneForm.appendChild(makeElement('input', null, {type: 'submit', value: 'Go'}));
            popup.appendChild(geneForm);
        
        
            geneForm.addEventListener('submit', function(ev) {
                ev.stopPropagation(); ev.preventDefault();
                var g = geneInput.value;
                thisB.removeAllPopups();

                if (!g || g.length == 0) {
                    return false;
                }

                thisB.searchEndpoint.features(null, {group: g, type: 'transcript'}, function(found) {        // HAXX
                    if (!found) found = [];
                    var min = 500000000, max = -100000000;
                    var nchr = null;
                    for (var fi = 0; fi < found.length; ++fi) {
                        var f = found[fi];

                        if (f.label != g) {
                            // ...because Dazzle can return spurious overlapping features.
                            continue;
                        }

                        if (nchr == null) {
                            nchr = f.segment;
                        }
                        min = Math.min(min, f.min);
                        max = Math.max(max, f.max);
                    }

                    if (!nchr) {
                        alert("no match for '" + g + "' (NB. server support for search is currently rather limited...)");
                    } else {
                        thisB.highlightMin = min;
                        thisB.highlightMax = max;
                        thisB.makeHighlight();

                        var padding = Math.max(2500, (0.3 * (max - min + 1))|0);
                        thisB.setLocation(min - padding, max + padding, nchr);
                    }
                }, false);
                
                return false;
            }, false);
        }

    }, false);

  
    var addPopupHandle;
    addButton.addEventListener('mousedown', function(ev) {
        ev.stopPropagation(); ev.preventDefault();
        var showing = addPopupHandle && addPopupHandle.displayed;
        thisB.removeAllPopups();
        if (!showing) {
            addPopupHandle = thisB.showTrackAdder(ev);
        }
    }, false);

    // set up the resetter
    resetButton.addEventListener('mousedown', function(ev) {
        ev.stopPropagation(); ev.preventDefault();

        removeChildren(thisB.tierHolder);
        removeChildren(thisB.dasLabelHolder);
        thisB.tiers = [];
        thisB.sources = [];
        thisB.knownSpace = null;

        for (var t = 0; t < thisB.defaultSources.length; ++t) {
            var source = thisB.defaultSources[t];
            thisB.sources.push(source);
            thisB.makeTier(source);
        }
        thisB.arrangeTiers();
        thisB.highlightMin = thisB.highlightMax = -1;
        thisB.setLocation(thisB.defaultStart, thisB.defaultEnd, thisB.defaultChr);
    }, false);
        
    this.tierHolder = dasTiers;
    this.tiers = [];
    if (overrideSources) {
        this.sources = overrideSources;
    } else {
        this.sources = [];
        for (var si = 0; si < this.defaultSources.length; ++si) {
            var s = this.defaultSources[si];
            if (!s.disable) {
                this.sources.push(s);
            }
        }
    }
    for (var t = 0; t < this.sources.length; ++t) {
        var source = this.sources[t];
        if (source.bwgURI && !this.supportsBinary) {
            if (!this.binaryWarningGiven) {
                this.popit({clientX: 300, clientY: 100}, 'Warning', makeElement('p', 'your browser does not support binary data formats, some track(s) not loaded.  We currently recommend Google Chrome 9 or later, or Firefox 4 or later.'));
                this.binaryWarningGiven = true;
            }
            continue;
        }
        this.makeTier(source);
    }
    thisB.arrangeTiers();
    
    //
    // Window resize support (should happen before first fetch so we know the actual size of the viewed area).
    //

    this.resizeViewer(true);
    window.addEventListener('resize', function(ev) {
        thisB.resizeViewer();
    }, false);

    //
    // Finalize initial viewable region, and kick off a fetch.
    //

    if (qChr && qMin && qMax) {
        this.chr = qChr; this.viewStart = qMin; this.viewEnd = qMax;
        if (this.highlightMin < 0) {
            this.highlightMin = qMin;  this.highlightMax = qMax;
        }
    }
    
    if ((this.viewEnd - this.viewStart) > MAX_VIEW_SIZE) {
        var mid = ((this.viewEnd + this.viewStart) / 2)|0;
        this.viewStart = mid - (MAX_VIEW_SIZE/2);
        this.viewEnd = mid + (MAX_VIEW_SIZE/2) - 1;
    }

    this.origin = ((this.viewStart + this.viewEnd) / 2) | 0;
    this.scale = this.featurePanelWidth / (this.viewEnd - this.viewStart);

    this.zoomExpt = 250 / Math.log(MAX_VIEW_SIZE / this.zoomBase);
    this.zoomSlider.setValue(this.zoomExpt * Math.log((this.viewEnd - this.viewStart + 1) / this.zoomBase));

    this.move(0); // will trigger a refresh() after failing spaceCheck.

    //
    // Tick-marks on the zoomer
    //

    this.makeZoomerTicks();

    // 
    // Set up interactivity handlers
    //

    this.__mouseMoveHandler = function(ev) {
        return thisB.mouseMoveHandler(ev);
    }
    this.__mouseUpHandler = function(ev) {
        return thisB.mouseUpHandler(ev);
    }
    main.addEventListener('mousedown', function(ev) {return thisB.mouseDownHandler(ev)}, false);

    main.addEventListener('touchstart', function(ev) {return thisB.touchStartHandler(ev)}, false);
    main.addEventListener('touchmove', function(ev) {return thisB.touchMoveHandler(ev)}, false);
    main.addEventListener('touchend', function(ev) {return thisB.touchEndHandler(ev)}, false);
    main.addEventListener('touchcancel', function(ev) {return thisB.touchCancelHandler(ev)}, false);

    this.svgRoot.addEventListener('mousewheel', function(ev) {   // FIXME does this need to be on the document?
        if (!ev.wheelDeltaX) {
            return;
        }

        ev.stopPropagation(); ev.preventDefault();
        thisB.move(-ev.wheelDeltaX/5);
    }, false);
    this.svgRoot.addEventListener('MozMousePixelScroll', function(ev) {
        if (ev.axis == 1) {
            ev.stopPropagation(); ev.preventDefault();
            if (ev.detail != 0) {
                thisB.move(ev.detail/4);
            }
        }
    }, false);

    var keyHandler = function(ev) {
//        dlog('keycode=' + ev.keyCode + '; charCode=' + ev.charCode);
        if (ev.keyCode == 13) {
            var layoutsChanged = false;
            for (var ti = 0; ti < thisB.tiers.length; ++ti) {
                var t = thisB.tiers[ti];
                if (t.wantedLayoutHeight && t.wantedLayoutHeight != t.layoutHeight) {
                    t.layoutHeight = t.wantedLayoutHeight;
                    t.placard = null;
                    t.clipTier();
                    layoutsChanged = true;
                }
            }
            if (layoutsChanged) {
                thisB.arrangeTiers();
            }
        } else if (ev.keyCode == 32 || ev.charCode == 32) {
            if (!thisB.snapZoomLockout) {
                if (!thisB.isSnapZooming) {
                    thisB.isSnapZooming = true;
                    var newZoom = thisB.savedZoom || 1.0;
                    thisB.savedZoom = thisB.zoomSlider.getValue();
                    thisB.zoomSlider.setValue(newZoom);
                    thisB.zoom(Math.exp((1.0 * newZoom) / thisB.zoomExpt));
                    thisB.invalidateLayouts();
                    thisB.zoomSlider.setColor('red');
                    thisB.refresh();
                } else {
                    thisB.isSnapZooming = false;
                    var newZoom = thisB.savedZoom || 10.0;
                    thisB.savedZoom = thisB.zoomSlider.getValue();
                    thisB.zoomSlider.setValue(newZoom);
                    thisB.zoom(Math.exp((1.0 * newZoom) / thisB.zoomExpt));
                    thisB.invalidateLayouts();
                    thisB.zoomSlider.setColor('blue');
                    thisB.refresh();
                }
                thisB.snapZoomLockout = true;
            }
            ev.stopPropagation(); ev.preventDefault();      
        } else if (ev.keyCode == 39 || ev.keyCode == 68) {
            ev.stopPropagation(); ev.preventDefault();
            if (ev.ctrlKey) {
                var fedge = 0;
                if(ev.shiftKey){
                    fedge = 1;
                }
                var pos=((thisB.viewStart + thisB.viewEnd + 1)/2)|0;
                thisB.tiers[thisB.selectedTier].findNextFeature(
                      thisB.chr,
                      pos,
                      -1,
                      fedge,
                      function(nxt) {
                          if (nxt) {
                              var nmin = nxt.min;
                              var nmax = nxt.max;
                              if (fedge) {
                                  if (nmax<pos-1) {
                                      nmax++;
                                      nmin=nmax;
                                  } else {
                                      nmax=nmin;
                                  }
                              }
                              var wid = thisB.viewEnd - thisB.viewStart + 1;
                              if(parseFloat(wid/2) == parseInt(wid/2)){wid--;}
                              var newStart = (nmin + nmax - wid)/2 + 1;
                              var newEnd = newStart + wid - 1;
                              var pos2=pos;
                              thisB.setLocation(newStart, newEnd, nxt.segment);
                          } else {
                              dlog('no next feature');
                          }
                      });
            } else {
                thisB.move(ev.shiftKey ? 100 : 25);
            }
        } else if (ev.keyCode == 37 || ev.keyCode == 65) {
            ev.stopPropagation(); ev.preventDefault();
            if (ev.ctrlKey) {
                var fedge = 0;
                if(ev.shiftKey){
                    fedge = 1;
                }
                var pos=((thisB.viewStart + thisB.viewEnd + 1)/2)|0;
                thisB.tiers[thisB.selectedTier].findNextFeature(
                      thisB.chr,
                      pos,
                      1,
                      fedge,
                      function(nxt) {
                          if (nxt) {
                              var nmin = nxt.min;
                              var nmax = nxt.max;
                              if (fedge) { 
                                  if (nmin>pos+1) {
                                      nmax=nmin;
                                  } else {
                                      nmax++;
                                      nmin=nmax
                                  }
                              }
                              var wid = thisB.viewEnd - thisB.viewStart + 1;
                              if(parseFloat(wid/2) == parseInt(wid/2)){wid--;}
                              var newStart = (nmin + nmax - wid)/2 + 1;
                              var newEnd = newStart + wid - 1;
                              var pos2=pos;
                              thisB.setLocation(newStart, newEnd, nxt.segment);
                          } else {
                              dlog('no next feature');
                          }
                      });
            } else {
                thisB.move(ev.shiftKey ? -100 : -25);
            }
        } else if (ev.keyCode == 38 || ev.keyCode == 87) {
            ev.stopPropagation(); ev.preventDefault();
            if (thisB.selectedTier > 0) {
                --thisB.selectedTier;
                thisB.tiers[thisB.selectedTier].isLabelValid = false;
                thisB.tiers[thisB.selectedTier + 1].isLabelValid = false;
                thisB.arrangeTiers();
            }
        } else if (ev.keyCode == 40 || ev.keyCode == 83) {
            ev.stopPropagation(); ev.preventDefault();
            if (thisB.selectedTier < thisB.tiers.length -1) {
                ++thisB.selectedTier;
                thisB.tiers[thisB.selectedTier].isLabelValid = false;
                thisB.tiers[thisB.selectedTier - 1].isLabelValid = false;
                thisB.arrangeTiers();
            }
        } else if (ev.charCode == 61) {
            ev.stopPropagation(); ev.preventDefault();

            var oz = thisB.zoomSlider.getValue();
            thisB.zoomSlider.setValue(oz - 10);
            var nz = thisB.zoomSlider.getValue();
            if (nz != oz) {
                thisB.zoom(Math.exp((1.0 * nz) / thisB.zoomExpt));
                thisB.scheduleRefresh(500);
            }
        } else if (ev.charCode == 45) {
            ev.stopPropagation(); ev.preventDefault();

            var oz = thisB.zoomSlider.getValue();
            thisB.zoomSlider.setValue(oz + 10);
            var nz = thisB.zoomSlider.getValue();
            if (nz != oz) {
                thisB.zoom(Math.exp((1.0 * nz) / thisB.zoomExpt));
                thisB.scheduleRefresh(500);
            }
        } else if (ev.keyCode == 84 || ev.keyCode == 116) {
            ev.stopPropagation(); ev.preventDefault();
            var bumpStatus;
            if( ev.shiftKey ){
                for (var ti = 0; ti < thisB.tiers.length; ++ti) {
                    var t = thisB.tiers[ti];
                    if (t.dasSource.collapseSuperGroups) {
                        if (bumpStatus === undefined) {
                            bumpStatus = !t.bumped;
                        }
                        t.bumped = bumpStatus;
                        t.isLabelValid = false;
                        t.layoutWasDone = false;
                        t.draw();
                    }
                }
            } else {
                var t = thisB.tiers[thisB.selectedTier];
                if (t.dasSource.collapseSuperGroups) {
                    if (bumpStatus === undefined) {
                        bumpStatus = !t.bumped;
                    }
                    t.bumped = bumpStatus;
                    t.layoutWasDone = false;
                    t.isLabelValid = false;
                    t.draw();
                }
            }
        } else {
            //dlog('key: ' + ev.keyCode)
        }
    };
    var keyUpHandler = function(ev) {

        thisB.snapZoomLockout = false;
/*
        if (ev.keyCode == 32) {
            if (thisB.isSnapZooming) {
                thisB.isSnapZooming = false;
                thisB.zoomSlider.setValue(thisB.savedZoom);
                thisB.zoom(Math.exp((1.0 * thisB.savedZoom / thisB.zoomExpt)));
                thisB.invalidateLayouts();
                thisB.refresh();
            }
            ev.stopPropagation(); ev.preventDefault();
        } */
    }

    var mouseLeaveHandler;
    mouseLeaveHandler = function(ev) {
        window.removeEventListener('keydown', keyHandler, false);
        window.removeEventListener('keyup', keyUpHandler, false);
        window.removeEventListener('keypress', keyHandler, false);
        thisB.svgRoot.removeEventListener('mouseout', mouseLeaveHandler, false);
    }

    this.svgRoot.addEventListener('mouseover', function(ev) {
        window.addEventListener('keydown', keyHandler, false);
        window.addEventListener('keyup', keyUpHandler, false);
        window.addEventListener('keypress', keyHandler, false);
        thisB.svgRoot.addEventListener('mouseout', mouseLeaveHandler, false);
    }, false);
    
    // Low-priority stuff
    this.storeStatus();   // to make sure things like resets are permanent.

    var epSource;
    for (var ti = 0; ti < this.tiers.length; ++ti) {
        var s = this.tiers[ti].dasSource;
        if (s.provides_entrypoints) {
            epSource = this.tiers[ti].dasSource;
            break;
        }
    }
    if (epSource) {
        epSource.entryPoints(
            function(ep) {
                thisB.entryPoints = ep;
                for (var epi = 0; epi < thisB.entryPoints.length; ++epi) {
                    if (thisB.entryPoints[epi].name == thisB.chr) {
                        thisB.currentSeqMax = thisB.entryPoints[epi].end;
                        break;
                    }
                }
            }
        );
    }

    thisB.queryRegistry(null, true);
    for (var m in this.chains) {
        this.queryRegistry(m, true);
    }
}

function setSources(msh, availableSources, maybeMapping) {
    if (maybeMapping) {
        for (var s = 0; s < availableSources.length; ++s) {
            availableSources[s].mapping = maybeMapping;
        }
    }
    msh.set(availableSources);
}

Browser.prototype.queryRegistry = function(maybeMapping, tryCache) {
    var thisB = this;
    var coords, msh;
    if (maybeMapping) {
        coords = this.chains[maybeMapping].coords;
        if (!thisB.mappableSources[maybeMapping]) {
            thisB.mappableSources[maybeMapping] = new Observed();
        }
        msh = thisB.mappableSources[maybeMapping];
    } else {
        coords = this.coordSystem;
        msh = this.availableSources;
    }
    var cacheHash = hex_sha1(miniJSONify(coords));
    if (tryCache) {
        var cacheTime = localStorage['dalliance.registry.' + cacheHash + '.last_queried'];
        if (cacheTime) {
            try {
                setSources(msh, JSON.parse(localStorage['dalliance.registry.' + cacheHash + '.sources']), maybeMapping);
                var cacheAge = (Date.now()|0) - (cacheTime|0);
                if (cacheAge < (12 * 60 * 60 * 1000)) {
                    // alert('Using cached registry data');
                    return;
                } else {
                    // alert('Registry data is stale, refetching');
                }
            } catch (rex) {
                dlog('Bad registry cache: ' + rex);
            }
        }
    }
            
    new DASRegistry(this.registry).sources(function(sources) {
        var availableSources = [];
        for (var s = 0; s < sources.length; ++s) {
            var source = sources[s];
            if (!source.coords || source.coords.length == 0) {
                continue;
            }
            var scoords = source.coords[0];
            if (scoords.taxon != coords.taxon || scoords.auth != coords.auth || scoords.version != coords.version) {
                continue;
            }   
            availableSources.push(source);
        }

        localStorage['dalliance.registry.' + cacheHash + '.sources'] = JSON.stringify(availableSources);
        localStorage['dalliance.registry.' + cacheHash + '.last_queried'] = '' + Date.now();
        
        setSources(msh, availableSources, maybeMapping);
    }, function(error) {
        // msh.set(null);
    }, coords);
}

Browser.prototype.makeTier = function(source) {
    try {
        this.realMakeTier(source);
    } catch (err) {
        dlog('Error creating tier: ' + err);
        // ...and continue.
    }
}

Browser.prototype.realMakeTier = function(source) {
    var viewport = document.createElementNS(NS_SVG, 'g');
    var viewportBackground = document.createElementNS(NS_SVG, 'rect');
    var col = this.tierBackgroundColors[this.tiers.length % this.tierBackgroundColors.length];
    viewportBackground.setAttribute('fill', col);
    viewportBackground.setAttribute('x', "-1000000");
    viewportBackground.setAttribute('y', "0");
    viewportBackground.setAttribute('width', "2000000");
    viewportBackground.setAttribute('height', "200");
    viewportBackground.setAttribute('stroke-width', "0");
    viewport.appendChild(viewportBackground);
    viewport.setAttribute("transform", "translate(200, " + ((2 * 200) + 50) + ")");
    
    var tier = new DasTier(this, source, viewport, viewportBackground);
    tier.init(); // fetches stylesheet

    this.tierHolder.appendChild(viewport);    
    this.tiers.push(tier);  // NB this currently tells any extant knownSpace about the new tier.
    this.refreshTier(tier);
    this.arrangeTiers();
}

Browser.prototype.removeTier = function(tier) {
    var ti = arrayIndexOf(this.tiers, tier);
    if (ti < 0) {
        return dlog("Couldn't find tier");
    }
            
    var deadTier = this.tiers[ti];
    this.tierHolder.removeChild(deadTier.viewport);
    if (deadTier.label) {
        this.dasLabelHolder.removeChild(deadTier.label);
    }
            
    this.tiers.splice(ti, 1);
    for (var nti = 0; nti < this.tiers.length; ++nti) {
        this.tiers[nti].background.setAttribute("fill", this.tierBackgroundColors[nti % this.tierBackgroundColors.length]);
        this.tiers[nti].isLabelValid = false;
    }

    this.arrangeTiers();
    this.storeStatus();
}

Browser.prototype.makeZoomerTicks = function() {
    var thisB = this;
    removeChildren(this.zoomTickMarks);

    var makeSliderMark = function(markSig) {
        var markPos = thisB.zoomExpt * Math.log(markSig/thisB.zoomBase);
        if (markPos < 0 || markPos > 250) {
            return;
        }
        var smark = makeElementNS(NS_SVG, 'line', null, {
            x1: 30 + markPos,
            y1: 35,
            x2: 30 + markPos,
            y2: 38,
            stroke: 'gray',
            strokeWidth: 1
        });
        var markText;
        if (markSig > 1500) {
            markText = '' + (markSig/1000) + 'kb';
        } else {
            markText= '' + markSig + 'bp';
        }
        var slabel = makeElementNS(NS_SVG, 'text', markText, {
            x: 30 + markPos,
            y: 48,
            fontSize: '8pt',
            stroke: 'none'
        });
        thisB.zoomTickMarks.appendChild(smark);
        thisB.zoomTickMarks.appendChild(slabel);
        // slabel.setAttribute('x', 29 + markPos - (slabel.getBBox().width/2));
        slabel.setAttribute('text-anchor', 'middle');
    }

    makeSliderMark(1000000);
    makeSliderMark(500000);
    makeSliderMark(100000);
    makeSliderMark(20000);
    makeSliderMark(4000);
    makeSliderMark(500);
    makeSliderMark(100);
    makeSliderMark(50);
}


Browser.prototype.resizeViewer = function(skipRefresh) {
    var width = this.svgHolder.offsetWidth;
    width = Math.max(width, 640);

    if (this.forceWidth) {
        width = this.forceWidth;
    }

    if (this.center) {
        this.svgHolder.style['margin-left'] = (((window.innerWidth - width) / 2)|0) + 'px';
    }

    this.svgRoot.setAttribute('width', width - 30);
    this.svgBackground.setAttribute('width', width - 30);
    this.featureClipRect.setAttribute('width', width - this.tabMargin - 40);
    this.featureBackground.setAttribute('width', width - this.tabMargin - 40);

    this.zoomWidget.setAttribute('transform', 'translate(' + (width - this.zoomSlider.width - 100) + ', 0)');
    if (width < 1075) {
        this.karyo.svg.setAttribute('transform', 'translate(2000, 15)');
    } else {
        this.karyo.svg.setAttribute('transform', 'translate(450, 20)');
    }
    this.regionLabelMax = (width - this.zoomSlider.width - 120)
    var oldFPW = this.featurePanelWidth;
    this.featurePanelWidth = (width - this.tabMargin - 40)|0;
    
    if (oldFPW != this.featurePanelWidth) {
        var viewWidth = this.viewEnd - this.viewStart;
        var nve = this.viewStart + (viewWidth * this.featurePanelWidth) / oldFPW;
        var delta = nve - this.viewEnd;
        this.viewStart = this.viewStart - (delta/2);
        this.viewEnd = this.viewEnd + (delta/2);

        var wid = this.viewEnd - this.viewStart + 1;
        if (this.currentSeqMax > 0 && this.viewEnd > this.currentSeqMax) {
            this.viewEnd = this.currentSeqMax;
            this.viewStart = this.viewEnd - wid + 1;
        }
        if (this.viewStart < 1) {
            this.viewStart = 1;
            this.viewEnd = this.viewStart + wid - 1;
        }
    
        this.xfrmTiers((this.tabMargin - (1.0 * (this.viewStart - this.origin)) * this.scale), 1);
        this.updateRegion();
        if (!skipRefresh) {
            this.spaceCheck();
        }
    }

    if (this.fgGuide) {
        this.fgGuide.setAttribute('x1', (this.featurePanelWidth/2) + this.tabMargin);
        this.fgGuide.setAttribute('x2', (this.featurePanelWidth/2) + this.tabMargin);
    }
        

    for (var pi = 0; pi < this.placards.length; ++pi) {
        var placard = this.placards[pi];
        var rects = placard.getElementsByTagName('rect');
        if (rects.length > 0) {
            rects[0].setAttribute('width', this.featurePanelWidth);
        }
    }
}

Browser.prototype.xfrmTiers = function(x, xs) {
    for (var ti = 0; ti < this.tiers.length; ++ti) {
        this.xfrmTier(this.tiers[ti], x, xs);
    }
    if (this.highlight) {
        var axs = xs;
        if (axs < 0) {
            axs = this.scale;
        }
        var xfrm = 'translate(' + x + ',0)';
        this.highlight.setAttribute('transform', xfrm);
        this.highlight.setAttribute('x', (this.highlightMin - this.origin) * this.scale);
        this.highlight.setAttribute('width', (this.highlightMax - this.highlightMin + 1) * this.scale);
    } 
}

Browser.prototype.jiggleLabels = function(tier) {
        var x = tier.xfrmX;
        var labels = tier.viewport.getElementsByClassName("label-text");
        for (var li = 0; li < labels.length; ++li) {
            var label = labels[li];
            if (label.jiggleMin && label.jiggleMax) {
                label.setAttribute('x', Math.min(Math.max(this.tabMargin - x, label.jiggleMin), label.jiggleMax));
            }
        }
}
        
Browser.prototype.xfrmTier = function(tier, x , xs) {
    if (tier.originHaxx && tier.originHaxx != 0) {
        x -= ((1.0 * tier.originHaxx) * this.scale);
    }
   
    var axs = xs;
    if (axs < 0) {
        axs = tier.scale;
    } else {
        tier.scale = xs;
    }

    var y = tier.y;
        
    if (x != tier.xfrmX || y != tier.xfrmY || axs != tier.xfrmS) {
        var xfrm = 'translate(' + x + ',' + tier.y + ')';
        if (axs != 1) {
            xfrm += ', scale(' + axs + ',1)';
        }
        tier.viewport.setAttribute('transform', xfrm);
    }
    if (tier.label && (y != tier.xfrmY || !tier.isLabelValid)) {
        tier.label.setAttribute('transform', 'translate(0, ' + y + ')');
        tier.isLabelValid = true;
    }

    tier.xfrmX = x;
    tier.xfrmY = y;
    tier.xfrmS = axs;

    this.jiggleLabels(tier);
}

//
// Navigation prims.
//

Browser.prototype.spaceCheck = function(dontRefresh) {
    if (!this.knownSpace || this.knownSpace.chr !== this.chr) {
        this.refresh();
        return;
    } 

    var width = ((this.viewEnd - this.viewStart)|0) + 1;
    var minExtraW = (width * this.minExtra) | 0;
    var maxExtraW = (width * this.maxExtra) | 0;
    if ((this.drawnStart|0) > Math.max(1, ((this.viewStart|0) - minExtraW)|0)  || (this.drawnEnd|0) < Math.min((this.viewEnd|0) + minExtraW, ((this.currentSeqMax|0) > 0 ? (this.currentSeqMax|0) : 1000000000)))  {
//         this.drawnStart = Math.max(1, (this.viewStart|0) - maxExtraW);
//        this.drawnEnd = Math.min((this.viewEnd|0) + maxExtraW, ((this.currentSeqMax|0) > 0 ? (this.currentSeqMax|0) : 1000000000));
        this.refresh();
    }
}

Browser.prototype.move = function(pos)
{
    var wid = this.viewEnd - this.viewStart;
    this.viewStart -= pos / this.scale;
    this.viewEnd = this.viewStart + wid;
    if (this.currentSeqMax > 0 && this.viewEnd > this.currentSeqMax) {
        this.viewEnd = this.currentSeqMax;
        this.viewStart = this.viewEnd - wid;
    }
    if (this.viewStart < 1) {
        this.viewStart = 1;
        this.viewEnd = this.viewStart + wid;
    }
    
    this.xfrmTiers((this.tabMargin - (1.0 * (this.viewStart - this.origin)) * this.scale), 1);
    this.updateRegion();
    this.karyo.update(this.chr, this.viewStart, this.viewEnd);
    this.spaceCheck();
}

Browser.prototype.zoom = function(factor) {
    this.zoomFactor = factor;
    var viewCenter = Math.round((this.viewStart + this.viewEnd) / 2.0)|0;
    this.viewStart = viewCenter - this.zoomBase * this.zoomFactor / 2;
    this.viewEnd = viewCenter + this.zoomBase * this.zoomFactor / 2;
    if (this.currentSeqMax > 0 && (this.viewEnd > this.currentSeqMax + 5)) {
        var len = this.viewEnd - this.viewStart + 1;
        this.viewEnd = this.currentSeqMax;
        this.viewStart = this.viewEnd - len + 1;
    }
    if (this.viewStart < 1) {
        var len = this.viewEnd - this.viewStart + 1;
        this.viewStart = 1;
        this.viewEnd = this.viewStart + len - 1;
    }
    this.scale = this.featurePanelWidth / (this.viewEnd - this.viewStart)
    this.updateRegion();

    var width = this.viewEnd - this.viewStart + 1;
    
    var scaleRat = (this.scale / this.scaleAtLastRedraw);
    this.xfrmTiers(this.tabMargin - ((1.0 * (this.viewStart - this.origin)) * this.scale),  (this.scale / this.scaleAtLastRedraw));

    var labels = this.svgRoot.getElementsByClassName("label-text");
    for (var li = 0; li < labels.length; ++li) {
        var label = labels[li];
        var x = label.getAttribute("x");
        var xfrm = "scale(" + (this.scaleAtLastRedraw/this.scale) + ",1), translate( " + ((x*this.scale - x*this.scaleAtLastRedraw) /this.scaleAtLastRedraw) +",0)";
        label.setAttribute("transform", xfrm);
    }
}

Browser.prototype.setLocation = function(newMin, newMax, newChr) {
    newMin = newMin|0;
    newMax = newMax|0;

    if (newChr && (newChr != this.chr)) {
        if (!this.entryPoints) {
            throw 'Need entry points';
        }
        var ep = null;
        for (var epi = 0; epi < this.entryPoints.length; ++epi) {
            var epName = this.entryPoints[epi].name;
            if (epName === newChr || ('chr' + epName) === newChr || epName === ('chr' + newChr)) {
                ep = this.entryPoints[epi];
                break;
            }
        }
        if (!ep) {
            throw "Couldn't find chromosome " + newChr;
        }

        this.chr = ep.name;
        this.currentSeqMax = ep.end;
    }

    var newWidth = newMax - newMin + 1;
    if (newWidth > MAX_VIEW_SIZE) {
        newMin = ((newMax + newMin - MAX_VIEW_SIZE)/2)|0;
        newMax = (newMin + MAX_VIEW_SIZE - 1)|0;
    }
    if (newWidth < this.zoomBase) {
        newMin = ((newMax + newMin - this.zoomBase)/2)|0;
        mewMax = (newMin + this.zoomBase - 1)|0;
    }

    if (newMin < 1) {
        var wid = newMax - newMin + 1;
        newMin = 1;
        newMax = Math.min(newMin + wid - 1, this.currentSeqMax);
    }
    if (this.currentSeqMax > 0 && newMax > this.currentSeqMax) {
        var wid = newMax - newMin + 1;
        newMax = this.currentSeqMax;
        newMin = Math.max(1, newMax - wid + 1);
    }

    this.viewStart = newMin|0;
    this.viewEnd = newMax|0;
    this.scale = this.featurePanelWidth / (this.viewEnd - this.viewStart);
    this.zoomSlider.setValue(this.zoomExpt * Math.log((this.viewEnd - this.viewStart + 1) / this.zoomBase));

    this.updateRegion();
    this.karyo.update(this.chr, this.viewStart, this.viewEnd);
    this.spaceCheck();
    this.xfrmTiers(this.tabMargin - ((1.0 * (this.viewStart - this.origin)) * this.scale), 1);   // FIXME currently needed to set the highlight (!)
    this.storeStatus();
}


Browser.prototype.storeStatus = function(){
    if (!this.cookieKey || this.noPersist) {
        return;
    }

    localStorage['dalliance.' + this.cookieKey + '.view-chr'] = this.chr;
    localStorage['dalliance.' + this.cookieKey + '.view-start'] = this.viewStart|0;
    localStorage['dalliance.' + this.cookieKey + '.view-end'] = this.viewEnd|0

    var currentSourceList = [];
    for (var t = 0; t < this.tiers.length; ++t) {
        var ts = this.tiers[t].dasSource;
        if (!ts.noPersist) {
            currentSourceList.push(this.tiers[t].dasSource);
        }
    }
    localStorage['dalliance.' + this.cookieKey + '.sources'] = JSON.stringify(currentSourceList);
    localStorage['dalliance.' + this.cookieKey + '.version'] = VERSION.CONFIG;
}

Browser.prototype.scheduleRefresh = function(time) {
    if (!time) {
        time = 500;
    }
    var thisB = this;

    if (this.refreshTB) {
        clearTimeout(this.refreshTB);
    }
    this.refreshTB = setTimeout(function() {
        thisB.refreshTB = null;
        thisB.refresh();
    }, time);
}

Browser.prototype.invalidateLayouts = function() {
    for (var t = 0; t < this.tiers.length; ++t) {
        this.tiers[t].layoutWasDone = false;
    }
}

Browser.prototype.refreshTier = function(tier) {
    if (this.knownSpace) {
        this.knownSpace.invalidate(tier);
    }
}/* -*- mode: javascript; c-basic-offset: 4; indent-tabs-mode: nil -*- */

// 
// Dalliance Genome Explorer
// (c) Thomas Down 2006-2010
//
// chainset.js: liftover support
//

function Chainset(uri, srcTag, destTag, coords) {
    this.uri = uri;
    this.srcTag = srcTag;
    this.destTag = destTag;
    this.coords = coords;
    this.chainsBySrc = {};
    this.chainsByDest = {};
    this.postFetchQueues = {};
}

function parseCigar(cigar)
{
    var cigops = [];
    var CIGAR_REGEXP = new RegExp('([0-9]*)([MID])', 'g');
    var match;
    while ((match = CIGAR_REGEXP.exec(cigar)) != null) {
        var count = match[1];
        if (count.length == 0) {
            count = 1;
        }
        cigops.push({cnt: count|0, op: match[2]});
    }
    return cigops;
}

Chainset.prototype.fetchChainsTo = function(chr) {
    var thisCS = this;
    new DASSource(this.uri).alignments(chr, {}, function(aligns) {
        if (!thisCS.chainsByDest[chr]) {
            thisCS.chainsByDest[chr] = []; // prevent re-fetching.
        }

        for (var ai = 0; ai < aligns.length; ++ai) {
            var aln = aligns[ai];
            for (var bi = 0; bi < aln.blocks.length; ++bi) {
                var block = aln.blocks[bi];
                var srcSeg, destSeg;
                for (var si = 0; si < block.segments.length; ++si) {
                    var seg = block.segments[si];
                    var obj = aln.objects[seg.object];
                    if (obj.dbSource === thisCS.srcTag) {
                        srcSeg = seg;
                    } else if (obj.dbSource === thisCS.destTag) {
                        destSeg = seg;
                    }
                }
                if (srcSeg && destSeg) {
                    var chain = {
                        srcChr:     aln.objects[srcSeg.object].accession,
                        srcMin:     srcSeg.min|0,
                        srcMax:     srcSeg.max|0,
                        srcOri:     srcSeg.strand,
                        destChr:    aln.objects[destSeg.object].accession,
                        destMin:    destSeg.min|0,
                        destMax:    destSeg.max|0,
                        destOri:    destSeg.strand,
                        blocks:     []
                    }

                    var srcops = parseCigar(srcSeg.cigar), destops = parseCigar(destSeg.cigar);
                    var srcOffset = 0, destOffset = 0;
                    var srci = 0, desti = 0;
                    while (srci < srcops.length && desti < destops.length) {
                        if (srcops[srci].op == 'M' && destops[desti].op == 'M') {
                            var blockLen = Math.min(srcops[srci].cnt, destops[desti].cnt);
                            chain.blocks.push([srcOffset, destOffset, blockLen]);
                            if (srcops[srci].cnt == blockLen) {
                                ++srci;
                            } else {
                                srcops[srci].cnt -= blockLen;
                            }
                            if (destops[desti].cnt == blockLen) {
                                ++desti;
                            } else {
                                destops[desti] -= blockLen;
                            }
                            srcOffset += blockLen;
                            destOffset += blockLen;
                        } else if (srcops[srci].op == 'I') {
                            destOffset += srcops[srci++].cnt;
                        } else if (destops[desti].op == 'I') {
                            srcOffset += destops[desti++].cnt;
                        }
                    }

                    pusho(thisCS.chainsBySrc, chain.srcChr, chain);
                    pusho(thisCS.chainsByDest, chain.destChr, chain);
                }
            }
        }

        if (thisCS.postFetchQueues[chr]) {
            var pfq = thisCS.postFetchQueues[chr];
            for (var i = 0; i < pfq.length; ++i) {
                pfq[i]();
            }
            thisCS.postFetchQueues[chr] = null;
        }
    });
}

Chainset.prototype.mapPoint = function(chr, pos) {
    var chains = this.chainsBySrc[chr] || [];
    for (var ci = 0; ci < chains.length; ++ci) {
        var c = chains[ci];
        if (pos >= c.srcMin && pos <= c.srcMax) {
            var cpos;
            if (c.srcOri == '-') {
                cpos = c.srcMax - pos;
            } else {
                cpos = pos - c.srcMin;
            }
            var blocks = c.blocks;
            for (var bi = 0; bi < blocks.length; ++bi) {
                var b = blocks[bi];
                var bSrc = b[0];
                var bDest = b[1];
                var bSize = b[2];
                if (cpos >= bSrc && cpos <= (bSrc + bSize)) {
                    var apos = cpos - bSrc;

                    var dpos;
                    if (c.destOri == '-') {
                        dpos = c.destMax - bDest - apos;
                    } else {
                        dpos = apos + bDest + c.destMin;
                    }
                    return {seq: c.destChr, pos: dpos, flipped: (c.srcOri != c.destOri)}
                }
            }
        }
    }
    return null;
}

Chainset.prototype.unmapPoint = function(chr, pos) {
    var chains = this.chainsByDest[chr] || [];
    for (var ci = 0; ci < chains.length; ++ci) {
        var c = chains[ci];
        if (pos >= c.destMin && pos <= c.destMax) {
            var cpos;
            if (c.srcOri == '-') {
                cpos = c.destMax - pos;
            } else {
                cpos = pos - c.destMin;
            }    
            
            var blocks = c.blocks;
            for (var bi = 0; bi < blocks.length; ++bi) {
                var b = blocks[bi];
                var bSrc = b[0];
                var bDest = b[1];
                var bSize = b[2];
                if (cpos >= bDest && cpos <= (bDest + bSize)) {
                    var apos = cpos - bDest;

                    var dpos = apos + bSrc + c.srcMin;
                    var dpos;
                    if (c.destOri == '-') {
                        dpos = c.srcMax - bSrc - apos;
                    } else {
                        dpos = apos + bSrc + c.srcMin;
                    }
                    return {seq: c.srcChr, pos: dpos, flipped: (c.srcOri != c.destOri)}
                }
            }
            return null;
        }
    }
    return null;
}

Chainset.prototype.sourceBlocksForRange = function(chr, min, max, callback) {
    if (!this.chainsByDest[chr]) {
        var fetchNeeded = !this.postFetchQueues[chr];
        var thisCS = this;
        pusho(this.postFetchQueues, chr, function() {
            thisCS.sourceBlocksForRange(chr, min, max, callback);
        });
        if (fetchNeeded) {
            this.fetchChainsTo(chr);
        }
    } else {
        var mmin = this.unmapPoint(chr, min);
        var mmax = this.unmapPoint(chr, max);
        if (!mmin || !mmax || mmin.seq != mmax.seq) {
            callback([]);
        } else {
            callback([new DASSegment(mmin.seq, mmin.pos, mmax.pos)]);
        }
    }
}
/* -*- mode: javascript; c-basic-offset: 4; indent-tabs-mode: nil -*- */

// 
// Dalliance Genome Explorer
// (c) Thomas Down 2006-2010
//
// das.js: queries and low-level data model.
//

var dasLibErrorHandler = function(errMsg) {
    alert(errMsg);
}
var dasLibRequestQueue = new Array();



function DASSegment(name, start, end, description) {
    this.name = name;
    this.start = start;
    this.end = end;
    this.description = description;
}
DASSegment.prototype.toString = function() {
    return this.name + ':' + this.start + '..' + this.end;
};
DASSegment.prototype.isBounded = function() {
    return this.start && this.end;
}
DASSegment.prototype.toDASQuery = function() {
    var q = 'segment=' + this.name;
    if (this.start && this.end) {
        q += (':' + this.start + ',' + this.end);
    }
    return q;
}


function DASSource(a1, a2) {
    var options;
    if (typeof a1 == 'string') {
        this.uri = a1;
        options = a2 || {};
    } else {
        options = a1 || {};
    }
    for (var k in options) {
        if (typeof(options[k]) != 'function') {
            this[k] = options[k];
        }
    }


    if (!this.coords) {
        this.coords = [];
    }
    if (!this.props) {
        this.props = {};
    }

    // if (!this.uri || this.uri.length == 0) {
    //    throw "URIRequired";
    // }   FIXME
    if (this.uri && this.uri.substr(this.uri.length - 1) != '/') {
        this.uri = this.uri + '/';
    }
}

function DASCoords() {
}

function coordsMatch(c1, c2) {
    return c1.taxon == c2.taxon && c1.auth == c2.auth && c1.version == c2.version;
}

//
// DAS 1.6 entry_points command
//

DASSource.prototype.entryPoints = function(callback) {
    var dasURI = this.uri + 'entry_points';
    this.doCrossDomainRequest(dasURI, function(responseXML) {
            if (!responseXML) {
                return callback([]);
            }

                var entryPoints = new Array();
                
                var segs = responseXML.getElementsByTagName('SEGMENT');
                for (var i = 0; i < segs.length; ++i) {
                    var seg = segs[i];
                    var segId = seg.getAttribute('id');
                    
                    var segSize = seg.getAttribute('size');
                    var segMin, segMax;
                    if (segSize) {
                        segMin = 1; segMax = segSize;
                    } else {
                        segMin = seg.getAttribute('start');
                        segMax = seg.getAttribute('stop');
                    }
                    var segDesc = null;
                    if (seg.firstChild) {
                        segDesc = seg.firstChild.nodeValue;
                    }
                    entryPoints.push(new DASSegment(segId, segMin, segMax, segDesc));
                }          
               callback(entryPoints);
    });         
}

//
// DAS 1.6 sequence command
// Do we need an option to fall back to the dna command?
//

function DASSequence(name, start, end, alpha, seq) {
    this.name = name;
    this.start = start;
    this.end = end;
    this.alphabet = alpha;
    this.seq = seq;
}

DASSource.prototype.sequence = function(segment, callback) {
    var dasURI = this.uri + 'sequence?' + segment.toDASQuery();
    this.doCrossDomainRequest(dasURI, function(responseXML) {
        if (!responseXML) {
            callback([]);
            return;
        } else {
                var seqs = new Array();
                
                var segs = responseXML.getElementsByTagName('SEQUENCE');
                for (var i = 0; i < segs.length; ++i) {
                    var seg = segs[i];
                    var segId = seg.getAttribute('id');
                    var segMin = seg.getAttribute('start');
                    var segMax = seg.getAttribute('stop');
                    var segAlpha = 'DNA';
                    var segSeq = null;
                    if (seg.firstChild) {
                        var rawSeq = seg.firstChild.nodeValue;
                        segSeq = '';
                        var idx = 0;
                        while (true) {
                            var space = rawSeq.indexOf('\n', idx);
                            if (space >= 0) {
                                segSeq += rawSeq.substring(idx, space);
                                idx = space + 1;
                            } else {
                                segSeq += rawSeq.substring(idx);
                                break;
                            }
                        }
                    }
                    seqs.push(new DASSequence(segId, segMin, segMax, segAlpha, segSeq));
                }
                
                callback(seqs);
        }
    });
}

//
// DAS 1.6 features command
//

function DASFeature() {
    // We initialize these in the parser...
}

function DASGroup() {
    // We initialize these in the parser, too...
}

function DASLink(desc, uri) {
    this.desc = desc;
    this.uri = uri;
}

DASSource.prototype.features = function(segment, options, callback) {
    options = options || {};

    var dasURI;
    //mpi2 edit to handle https as well as http
    if (this.uri.indexOf('http://') == 0 || this.uri.indexOf('https://') == 0) {
        var filters = [];

        if (segment) {
            filters.push(segment.toDASQuery());
        } else if (options.group) {
            var g = options.group;
            if (typeof g == 'string') {
                filters.push('group_id=' + g);
            } else {
                for (var gi = 0; gi < g.length; ++gi) {
                    filters.push('group_id=' + g[gi]);
                }
            }
        }

        if (options.adjacent) {
            var adj = options.adjacent;
            if (typeof adj == 'string') {
                adj = [adj];
            }
            for (var ai = 0; ai < adj.length; ++ai) {
                filters.push('adjacent=' + adj[ai]);
            }
        }

        if (options.type) {
            if (typeof options.type == 'string') {
                filters.push('type=' + options.type);
            } else {
                for (var ti = 0; ti < options.type.length; ++ti) {
                    filters.push('type=' + options.type[ti]);
                }
            }
        }
        
        if (options.maxbins) {
            filters.push('maxbins=' + options.maxbins);
        }
        
        if (filters.length > 0) {
            dasURI = this.uri + 'features?' + filters.join(';');
        } else {
            callback([], 'No filters specified');
        }
    } else {
        dasURI = this.uri;
    }
   

    this.doCrossDomainRequest(dasURI, function(responseXML, req) {
        if (!responseXML) {
            var msg;
            if (req.status == 0) {
                msg = 'server may not support CORS';
            } else {
                msg = 'status=' + req.status;
            }
            callback([], 'Failed request: ' + msg);
            return;
        }
/*      if (req) {
            var caps = req.getResponseHeader('X-DAS-Capabilties');
            if (caps) {
                alert(caps);
            }
        } */

        var features = new Array();
        var segmentMap = {};

        var segs = responseXML.getElementsByTagName('SEGMENT');
        for (var si = 0; si < segs.length; ++si) {
            var segmentXML = segs[si];
            var segmentID = segmentXML.getAttribute('id');
            segmentMap[segmentID] = {
                min: segmentXML.getAttribute('start'),
                max: segmentXML.getAttribute('stop')
            };
            
            var featureXMLs = segmentXML.getElementsByTagName('FEATURE');
            for (var i = 0; i < featureXMLs.length; ++i) {
                var feature = featureXMLs[i];
                var dasFeature = new DASFeature();
                
                dasFeature.segment = segmentID;
                dasFeature.id = feature.getAttribute('id');
                dasFeature.label = feature.getAttribute('label');
                var spos = elementValue(feature, "START");
                var epos = elementValue(feature, "END");
                if ((spos|0) > (epos|0)) {
                    dasFeature.min = epos|0;
                    dasFeature.max = spos|0;
                } else {
                    dasFeature.min = spos|0;
                    dasFeature.max = epos|0;
                }
                {
                    var tec = feature.getElementsByTagName('TYPE');
                    if (tec.length > 0) {
                        var te = tec[0];
                        if (te.firstChild) {
                            dasFeature.type = te.firstChild.nodeValue;
                        }
                        dasFeature.typeId = te.getAttribute('id');
                        dasFeature.typeCv = te.getAttribute('cvId');
                    }
                }
                dasFeature.type = elementValue(feature, "TYPE");
                if (!dasFeature.type && dasFeature.typeId) {
                    dasFeature.type = dasFeature.typeId; // FIXME?
                }
                
                dasFeature.method = elementValue(feature, "METHOD");
                {
                    var ori = elementValue(feature, "ORIENTATION");
                    if (!ori) {
                        ori = '0';
                    }
                    dasFeature.orientation = ori;
                }
                dasFeature.score = elementValue(feature, "SCORE");
                dasFeature.links = dasLinksOf(feature);
                dasFeature.notes = dasNotesOf(feature);
                
                var groups = feature.getElementsByTagName("GROUP");
                for (var gi  = 0; gi < groups.length; ++gi) {
                    var groupXML = groups[gi];
                    var dasGroup = new DASGroup();
                    dasGroup.type = groupXML.getAttribute('type');
                    dasGroup.id = groupXML.getAttribute('id');
                    dasGroup.links = dasLinksOf(groupXML);
                    dasGroup.notes = dasNotesOf(groupXML);
                    if (!dasFeature.groups) {
                        dasFeature.groups = new Array(dasGroup);
                    } else {
                        dasFeature.groups.push(dasGroup);
                    }
                }

                // Magic notes.  Check with TAD before changing this.
                if (dasFeature.notes) {
                    for (var ni = 0; ni < dasFeature.notes.length; ++ni) {
                        var n = dasFeature.notes[ni];
                        if (n.indexOf('Genename=') == 0) {
                            var gg = new DASGroup();
                            gg.type='gene';
                            gg.id = n.substring(9);
                            if (!dasFeature.groups) {
                                dasFeature.groups = new Array(gg);
                            } else {
                                dasFeature.groups.push(gg);
                            }
                        }
                    }
                }
                
                {
                    var pec = feature.getElementsByTagName('PART');
                    if (pec.length > 0) {
                        var parts = [];
                        for (var pi = 0; pi < pec.length; ++pi) {
                            parts.push(pec[pi].getAttribute('id'));
                        }
                        dasFeature.parts = parts;
                    }
                }
                {
                    var pec = feature.getElementsByTagName('PARENT');
                    if (pec.length > 0) {
                        var parents = [];
                        for (var pi = 0; pi < pec.length; ++pi) {
                            parents.push(pec[pi].getAttribute('id'));
                        }
                        dasFeature.parents = parents;
                    }
                }
                
                features.push(dasFeature);
            }
        }
                
        callback(features, undefined, segmentMap);
    });
}

function DASAlignment(type) {
    this.type = type;
    this.objects = {};
    this.blocks = [];
}

DASSource.prototype.alignments = function(segment, options, callback) {
    var dasURI = this.uri + 'alignment?query=' + segment;
    this.doCrossDomainRequest(dasURI, function(responseXML) {
        if (!responseXML) {
            callback([], 'Failed request ' + dasURI);
            return;
        }

        var alignments = [];
        var aliXMLs = responseXML.getElementsByTagName('alignment');
        for (var ai = 0; ai < aliXMLs.length; ++ai) {
            var aliXML = aliXMLs[ai];
            var ali = new DASAlignment(aliXML.getAttribute('alignType'));
            var objXMLs = aliXML.getElementsByTagName('alignObject');
            for (var oi = 0; oi < objXMLs.length; ++oi) {
                var objXML = objXMLs[oi];
                var obj = {
                    id:          objXML.getAttribute('intObjectId'),
                    accession:   objXML.getAttribute('dbAccessionId'),
                    version:     objXML.getAttribute('objectVersion'),
                    dbSource:    objXML.getAttribute('dbSource'),
                    dbVersion:   objXML.getAttribute('dbVersion')
                };
                ali.objects[obj.id] = obj;
            }
            
            var blockXMLs = aliXML.getElementsByTagName('block');
            for (var bi = 0; bi < blockXMLs.length; ++bi) {
                var blockXML = blockXMLs[bi];
                var block = {
                    order:      blockXML.getAttribute('blockOrder'),
                    segments:   []
                };
                var segXMLs = blockXML.getElementsByTagName('segment');
                for (var si = 0; si < segXMLs.length; ++si) {
                    var segXML = segXMLs[si];
                    var seg = {
                        object:      segXML.getAttribute('intObjectId'),
                        min:         segXML.getAttribute('start'),
                        max:         segXML.getAttribute('end'),
                        strand:      segXML.getAttribute('strand'),
                        cigar:       elementValue(segXML, 'cigar')
                    };
                    block.segments.push(seg);
                }
                ali.blocks.push(block);
            }       
                    
            alignments.push(ali);
        }
        callback(alignments);
    });
}


function DASStylesheet() {
/*
    this.highZoomStyles = new Object();
    this.mediumZoomStyles = new Object();
    this.lowZoomStyles = new Object();
*/

    this.styles = [];
}

DASStylesheet.prototype.pushStyle = function(filters, zoom, style) {
    /*

    if (!zoom) {
        this.highZoomStyles[type] = style;
        this.mediumZoomStyles[type] = style;
        this.lowZoomStyles[type] = style;
    } else if (zoom == 'high') {
        this.highZoomStyles[type] = style;
    } else if (zoom == 'medium') {
        this.mediumZoomStyles[type] = style;
    } else if (zoom == 'low') {
        this.lowZoomStyles[type] = style;
    }

    */

    if (!filters) {
        filters = {type: 'default'};
    }
    var styleHolder = shallowCopy(filters);
    if (zoom) {
        styleHolder.zoom = zoom;
    }
    styleHolder.style = style;
    this.styles.push(styleHolder);
}

function DASStyle() {
}

DASSource.prototype.stylesheet = function(successCB, failureCB) {
    var dasURI, creds = this.credentials;
    if (this.stylesheet_uri) {
        dasURI = this.stylesheet_uri;
        creds = false;
    } else {
        dasURI = this.uri + 'stylesheet';
    }

    doCrossDomainRequest(dasURI, function(responseXML) {
        if (!responseXML) {
            if (failureCB) {
                failureCB();
            } 
            return;
        }
        var stylesheet = new DASStylesheet();
        var typeXMLs = responseXML.getElementsByTagName('TYPE');
        for (var i = 0; i < typeXMLs.length; ++i) {
            var typeStyle = typeXMLs[i];
            
            var filter = {};
            filter.type = typeStyle.getAttribute('id'); // Am I right in thinking that this makes DASSTYLE XML invalid?  Ugh.
            filter.label = typeStyle.getAttribute('label');
            filter.method = typeStyle.getAttribute('method');
            var glyphXMLs = typeStyle.getElementsByTagName('GLYPH');
            for (var gi = 0; gi < glyphXMLs.length; ++gi) {
                var glyphXML = glyphXMLs[gi];
                var zoom = glyphXML.getAttribute('zoom');
                var glyph = childElementOf(glyphXML);
                var style = new DASStyle();
                style.glyph = glyph.localName;
                var child = glyph.firstChild;
        
                while (child) {
                    if (child.nodeType == Node.ELEMENT_NODE) {
                        // alert(child.localName);
                        style[child.localName] = child.firstChild.nodeValue;
                    }
                    child = child.nextSibling;
                }
                stylesheet.pushStyle(filter, zoom, style);
            }
        }
        successCB(stylesheet);
    }, creds);
}

//
// sources command
// 

function DASRegistry(uri, opts)
{
    opts = opts || {};
    this.uri = uri;
    this.opts = opts;   
}

DASRegistry.prototype.sources = function(callback, failure, opts)
{
    if (!opts) {
        opts = {};
    }

    var filters = [];
    if (opts.taxon) {
        filters.push('organism=' + opts.taxon);
    }
    if (opts.auth) {
        filters.push('authority=' + opts.auth);
    }
    if (opts.version) {
        filters.push('version=' + opts.version);
    }
    var quri = this.uri;
    if (filters.length > 0) {
        quri = quri + '?' + filters.join('&');   // '&' as a separator to hack around dasregistry.org bug.
    }

    doCrossDomainRequest(quri, function(responseXML) {
        if (!responseXML && failure) {
            failure();
            return;
        }

        var sources = [];       
        var sourceXMLs = responseXML.getElementsByTagName('SOURCE');
        for (var si = 0; si < sourceXMLs.length; ++si) {
            var sourceXML = sourceXMLs[si];
            var versionXMLs = sourceXML.getElementsByTagName('VERSION');
            if (versionXMLs.length < 1) {
                continue;
            }
            var versionXML = versionXMLs[0];

            var coordXMLs = versionXML.getElementsByTagName('COORDINATES');
            var coords = [];
            for (var ci = 0; ci < coordXMLs.length; ++ci) {
                var coordXML = coordXMLs[ci];
                var coord = new DASCoords();
                coord.auth = coordXML.getAttribute('authority');
                coord.taxon = coordXML.getAttribute('taxid');
                coord.version = coordXML.getAttribute('version');
                coords.push(coord);
            }
            
            var caps = [];
            var capXMLs = versionXML.getElementsByTagName('CAPABILITY');
            var uri;
            for (var ci = 0; ci < capXMLs.length; ++ci) {
                var capXML = capXMLs[ci];
                
                caps.push(capXML.getAttribute('type'));

                if (capXML.getAttribute('type') == 'das1:features') {
                    var fep = capXML.getAttribute('query_uri');
                    uri = fep.substring(0, fep.length - ('features'.length));
                }
            }

            var props = {};
            var propXMLs = versionXML.getElementsByTagName('PROP');
            for (var pi = 0; pi < propXMLs.length; ++pi) {
                pusho(props, propXMLs[pi].getAttribute('name'), propXMLs[pi].getAttribute('value'));
            }
            
            if (uri) {
                var source = new DASSource(uri, {
                    source_uri: sourceXML.getAttribute('uri'),
                    name:  sourceXML.getAttribute('title'),
                    desc:  sourceXML.getAttribute('description'),
                    coords: coords,
                    props: props,
                    capabilities: caps
                });
                sources.push(source);
            }
        }
        
        callback(sources);
    });
}


//
// Utility functions
//

function elementValue(element, tag)
{
    var children = element.getElementsByTagName(tag);
    if (children.length > 0 && children[0].firstChild) {
        return children[0].firstChild.nodeValue;
    } else {
        return null;
    }
}

function childElementOf(element)
{
    if (element.hasChildNodes()) {
        var child = element.firstChild;
        do {
            if (child.nodeType == Node.ELEMENT_NODE) {
                return child;
            } 
            child = child.nextSibling;
        } while (child != null);
    }
    return null;
}


function dasLinksOf(element)
{
    var links = new Array();
    var maybeLinkChilden = element.getElementsByTagName('LINK');
    for (var ci = 0; ci < maybeLinkChilden.length; ++ci) {
        var linkXML = maybeLinkChilden[ci];
        if (linkXML.parentNode == element) {
            links.push(new DASLink(linkXML.firstChild ? linkXML.firstChild.nodeValue : 'Unknown', linkXML.getAttribute('href')));
        }
    }
    
    return links;
}

function dasNotesOf(element)
{
    var notes = [];
    var maybeNotes = element.getElementsByTagName('NOTE');
    for (var ni = 0; ni < maybeNotes.length; ++ni) {
        if (maybeNotes[ni].firstChild) {
            notes.push(maybeNotes[ni].firstChild.nodeValue);
        }
    }
    return notes;
}

function doCrossDomainRequest(url, handler, credentials, custAuth) {
    // TODO: explicit error handlers?

    if (window.XDomainRequest) {
        var req = new XDomainRequest();
        req.onload = function() {
            var dom = new ActiveXObject("Microsoft.XMLDOM");
            dom.async = false;
            dom.loadXML(req.responseText);
            handler(dom);
        }
        req.open("get", url);
        req.send('');
    } else {
        var req = new XMLHttpRequest();

        req.onreadystatechange = function() {
            if (req.readyState == 4) {
              if (req.status >= 200 || req.status == 0) {
                  handler(req.responseXML, req);
              }
            }
        };
        req.open("get", url, true);
        if (credentials) {
            req.withCredentials = true;
        }
        if (custAuth) {
            req.setRequestHeader('X-DAS-Authorisation', custAuth);
        }
        req.setRequestHeader('Accept', 'application/xml,*/*');
        req.send('');
    }
}

DASSource.prototype.doCrossDomainRequest = function(url, handler) {
    var custAuth;
    if (this.xUser) {
        custAuth = 'Basic ' + btoa(this.xUser + ':' + this.xPass);
    }
    return doCrossDomainRequest(url, handler, this.credentials, custAuth);
}
/* -*- mode: javascript; c-basic-offset: 4; indent-tabs-mode: nil -*- */

// 
// Dalliance Genome Explorer
// (c) Thomas Down 2006-2010
//
// domui.js: SVG UI components
//

Browser.prototype.makeTooltip = function(ele, text)
{
    var isin = false;
    var thisB = this;
    var timer = null;
    var outlistener;
    outlistener = function(ev) {
        isin = false;
        if (timer) {
            clearTimeout(timer);
            timer = null;
        }
        ele.removeEventListener('mouseout', outlistener, false);
    };

    var setup;
    setup = function(ev) {
        var mx = ev.clientX + window.scrollX, my = ev.clientY + window.scrollY;
        if (!timer) {
            timer = setTimeout(function() {
                var popup = makeElement('div', text, {}, {
                    position: 'absolute',
                    top: '' + (my + 20) + 'px',
                    left: '' + Math.max(mx - 30, 20) + 'px',
                    backgroundColor: 'rgb(250, 240, 220)',
                    borderWidth: '1px',
                    borderColor: 'black',
                    borderStyle: 'solid',
                    padding: '2px',
                    maxWidth: '400px'
                });
                thisB.hPopupHolder.appendChild(popup);
                var moveHandler;
                moveHandler = function(ev) {
                    try {
                        thisB.hPopupHolder.removeChild(popup);
                    } catch (e) {
                        // May have been removed by other code which clears the popup layer.
                    }
                    window.removeEventListener('mousemove', moveHandler, false);
                    if (isin) {
                        if (ele.offsetParent == null) {
                            // dlog('Null parent...');
                        } else {
                            setup(ev);
                        }
                    }
                }
                window.addEventListener('mousemove', moveHandler, false);
                timer = null;
            }, 1000);
        }
    };

    ele.addEventListener('mouseover', function(ev) {
        isin = true
        ele.addEventListener('mouseout', outlistener, false);
        setup(ev);
    }, false);
    ele.addEventListener('DOMNodeRemovedFromDocument', function(ev) {
        isin = false;
        if (timer) {
            clearTimeout(timer);
            timer = null;
        }
    }, false);
}

Browser.prototype.popit = function(ev, name, ele, opts)
{
    var thisB = this;
    if (!opts) {
        opts = {};
    }

    var width = opts.width || 200;

    var mx =  ev.clientX, my = ev.clientY;
    mx +=  document.documentElement.scrollLeft || document.body.scrollLeft;
    my +=  document.documentElement.scrollTop || document.body.scrollTop;
    var winWidth = window.innerWidth;

    var top = (my + 30);
    var left = Math.min((mx - 30), (winWidth - width - 10));

    var popup = makeElement('div');
    popup.style.position = 'absolute';
    popup.style.top = '' + top + 'px';
    popup.style.left = '' + left + 'px';
    popup.style.width = width + 'px';
    popup.style.backgroundColor = 'white';
    popup.style.borderWidth = '2px';
    popup.style.borderColor = 'black'
    popup.style.borderStyle = 'solid';

    if (name) {
        var closeButton = makeElement('div', 'X', null, {
            marginTop: '-3px',
            padding: '3px',
            borderStyle: 'none',
            borderLeftStyle: 'solid',
            borderWidth: '1px',
            borderColor: 'rgb(128,128,128)',
            cssFloat: 'right'
        });
        closeButton.style['float'] = 'right';
        closeButton.addEventListener('mouseover', function(ev) {
            closeButton.style.color = 'red';
        }, false);
        closeButton.addEventListener('mouseout', function(ev) {
            closeButton.style.color = 'black';
        }, false);
        closeButton.addEventListener('mousedown', function(ev) {
            thisB.removeAllPopups();
        }, false);
        var tbar = makeElement('div', [makeElement('span', name, null, {maxWidth: '200px'}), closeButton], null, {
            backgroundColor: 'rgb(230,230,250)',
            borderColor: 'rgb(128,128,128)',
            borderStyle: 'none',
            borderBottomStyle: 'solid',
            borderWidth: '1px',
            padding: '3px'
        });

        var dragOX, dragOY;
        var moveHandler, upHandler;
        moveHandler = function(ev) {
            ev.stopPropagation(); ev.preventDefault();
            left = left + (ev.clientX - dragOX);
            if (left < 8) {
                left = 8;
            } if (left > (winWidth - width - 32)) {
                left = (winWidth - width - 26);
            }
            top = top + (ev.clientY - dragOY);
            top = Math.max(10, top);
            popup.style.top = '' + top + 'px';
            popup.style.left = '' + Math.min(left, (winWidth - width - 10)) + 'px';
            dragOX = ev.clientX; dragOY = ev.clientY;
        }
        upHandler = function(ev) {
            ev.stopPropagation(); ev.preventDefault();
            window.removeEventListener('mousemove', moveHandler, false);
            window.removeEventListener('mouseup', upHandler, false);
        }
        tbar.addEventListener('mousedown', function(ev) {
            ev.preventDefault(); ev.stopPropagation();
            dragOX = ev.clientX; dragOY = ev.clientY;
            window.addEventListener('mousemove', moveHandler, false);
            window.addEventListener('mouseup', upHandler, false);
        }, false);
                              

        popup.appendChild(tbar);
    }

    popup.appendChild(makeElement('div', ele, null, {
        padding: '3px',
        clear: 'both'
    }));
    this.hPopupHolder.appendChild(popup);

    var popupHandle = {
        node: popup,
        displayed: true
    };
    popup.addEventListener('DOMNodeRemoved', function(ev) {
        popupHandle.displayed = false;
    }, false);
    return popupHandle;
}

function IconSet(uri)
{
    var req = new XMLHttpRequest();
    req.open('get', uri, false);
    req.send();
    this.icons = req.responseXML;
}

IconSet.prototype.createIcon = function(name, parent)
{
    var master = this.icons.getElementById(name);
    if (!master) {
        alert("couldn't find " + name);
        return;
    }
    var copy = document.importNode(master, true);
    // parent.appendChild(copy);
    // var bbox = copy.getBBox();
    // parent.removeChild(copy);
    // copy.setAttribute('transform', 'translate(' + (-bbox.x)  + ',' + (-bbox.y)+ ')');
    var icon = makeElementNS(NS_SVG, 'g', copy);
    return icon;
}


IconSet.prototype.createButton = function(name, parent, bx, by)
{
    bx = bx|0;
    by = by|0;

    var master = this.icons.getElementById(name);
    var copy = document.importNode(master, true);
    // parent.appendChild(copy);
    // var bbox = copy.getBBox();
    // parent.removeChild(copy);
    // copy.setAttribute('transform', 'translate(' + (((bx - bbox.width - 2)/2) - bbox.x)  + ',' + (((by - bbox.height - 2)/2) - bbox.y)+ ')');
    var button = makeElementNS(NS_SVG, 'g', [
        makeElementNS(NS_SVG, 'rect', null, {
            x: 0,
            y: 0,
            width: bx,
            height: by,
            fill: 'rgb(230,230,250)',
            stroke: 'rgb(150,150,220)',
            strokeWidth: 2
        }), 
        copy ]);
    return button;
}

function dlog(msg) {
    var logHolder = document.getElementById('log');
    if (logHolder) {
        logHolder.appendChild(makeElement('p', msg));
    }
}
/* -*- mode: javascript; c-basic-offset: 4; indent-tabs-mode: nil -*- */

// 
// Dalliance Genome Explorer
// (c) Thomas Down 2006-2010
//
// feature-tier.js: renderers for glyphic data
//

var MIN_FEATURE_PX = 1; // FIXME: slightly higher would be nice, but requires making
                        // drawing of joined-up groups a bit smarter.   

var MIN_PADDING = 3;

var DEFAULT_SUBTIER_MAX = 25;

var NULL_BBOX = {x: 0, y: 0, width: 0, height: 0};

//
// Colour handling
//

function DColour(red, green, blue, name) {
    this.red = red|0;
    this.green = green|0;
    this.blue = blue|0;
    if (name) {
        this.name = name;
    }
}

DColour.prototype.toSvgString = function() {
    if (!this.name) {
        this.name = "rgb(" + this.red + "," + this.green + "," + this.blue + ")";
    }

    return this.name;
}

var palette = {
    red: new DColour(255, 0, 0, 'red'),
    green: new DColour(0, 255, 0, 'green'),
    blue: new DColour(0, 0, 255, 'blue'),
    yellow: new DColour(255, 255, 0, 'yellow'),
    white: new DColour(255, 255, 255, 'white'),
    black: new DColour(0, 0, 0, 'black')
};

var COLOR_RE = new RegExp('^#([0-9A-Fa-f]{2})([0-9A-Fa-f]{2})([0-9A-Fa-f]{2})$');

function dasColourForName(name) {
    var c = palette[name];
    if (!c) {
        var match = COLOR_RE.exec(name);
        if (match) {
            c = new DColour(('0x' + match[1])|0, ('0x' + match[2])|0, ('0x' + match[3])|0, name);
            palette[name] = c;
        } else {
            dlog("couldn't handle color: " + name);
            c = palette.black;
            palette[name] = c;
        }
    }
    return c;
}

// 
// Wrapper for glyph plus metrics
//

function DGlyph(glyph, min, max, height) {
    this.glyph = glyph;
    this.min = min;
    this.max = max;
    this.height = height;
    this.zindex = 0;
}

//
// Set of bumped glyphs
// 

function DSubTier() {
    this.glyphs = [];
    this.height = 0;
}

DSubTier.prototype.add = function(glyph) {
    this.glyphs.push(glyph);
    this.height = Math.max(this.height, glyph.height);
}

DSubTier.prototype.hasSpaceFor = function(glyph) {
    for (var i = 0; i < this.glyphs.length; ++i) {
        var g = this.glyphs[i];
        if (g.min <= glyph.max && g.max >= glyph.min) {
            return false;
        }
    }
    return true;
}

//
// Stylesheet handling (experimental 0.5.3 version)
//

DasTier.prototype.styleForFeature = function(f) {
    // dlog('styling ' + miniJSONify(f));

    var ssScale = zoomForScale(this.browser.scale);

    if (!this.stylesheet) {
        return null;
    }

    var maybe = null;
    var ss = this.stylesheet.styles;
    for (var si = 0; si < ss.length; ++si) {
        var sh = ss[si];
        if (sh.zoom && sh.zoom != ssScale) {
            continue;
        }
        if (sh.label && !(new RegExp('^' + sh.label + '$').test(f.label))) {
            continue;
        }
        if (sh.method && !(new RegExp('^' + sh.method + '$').test(f.method))) {
            continue;
        }
        if (sh.type) {
            if (sh.type == 'default') {
                if (!maybe) {
                    maybe = sh.style;
                }
                continue;
            } else if (sh.type != f.type) {
                continue;
            }
        }
        // perfect match.
        return sh.style;
    }
    return maybe;
}

function drawLine(featureGroupElement, features, style, tier, y)
{
    var origin = tier.browser.origin, scale = tier.browser.scale;
    var height = style.HEIGHT || 30;
    var min = tier.dasSource.forceMin || style.MIN || tier.currentFeaturesMinScore || 0;
    var max = tier.dasSource.forceMax || style.MAX || tier.currentFeaturesMaxScore || 10;
    var yscale = ((1.0 * height) / (max - min));
    var width = style.LINEWIDTH || 1;
    var color = style.COLOR || style.COLOR1 || 'black';

    var path = document.createElementNS(NS_SVG, 'path');
    path.setAttribute("fill", "none");
    path.setAttribute('stroke', color);
    path.setAttribute("stroke-width", width);
    var pathOps = '';

    for (var fi = 0; fi < features.length; ++fi) {
        var f = features[fi];

        var px = ((((f.min|0) + (f.max|0)) / 2) - origin) * scale;
        var sc = ((f.score - (1.0*min)) * yscale)|0;
        var py = y + (height - sc);
        if (fi == 0) {
            pathOps = 'M ' + px + ' ' + py;
        } else {
            pathOps += ' L ' + px + ' ' + py;
        }       
    }
    path.setAttribute('d', pathOps);
    featureGroupElement.appendChild(path);

    var clipId = 'line_clip_' + (++clipIdSeed);
    var clip = document.createElementNS(NS_SVG, 'clipPath');
    clip.setAttribute('id', clipId);
    var clipRect = document.createElementNS(NS_SVG, 'rect');
    clipRect.setAttribute('x', -500000);
    clipRect.setAttribute('y', y - 1);
    clipRect.setAttribute('width', 1000000);
    clipRect.setAttribute('height', height + 2);
    clip.appendChild(clipRect);
    featureGroupElement.appendChild(clip);
    path.setAttribute('clip-path', 'url(#' + clipId + ')');
   
    if (!tier.isQuantitative) {
        tier.isQuantitative = true;
        tier.isLabelValid = false;
    }
    if (tier.min != min) {
        tier.min = min;
        tier.isLabelValid = false;
    }
    if (tier.max != max) {
        tier.max = max;
        tier.isLabelValid = false;
    }
    if (tier.clientMin != y|0 + height) {
        tier.clientMin = y|0 + height;
        tier.isLabelValid = false;
    }
    if (tier.clientMax != y) {
        tier.clientMax = y;
        tier.isLabelValid = false;
    }

    return height|0 + MIN_PADDING;
}

function sortFeatures(tier)
{
    var ungroupedFeatures = {};
    var groupedFeatures = {};
    var groups = {};
    var superGroups = {};
    var groupsToSupers = {};
    var nonPositional = [];
    var minScore, maxScore;
    var fbid;

    var init_fbid = function() {
        fbid = {};
        for (var fi = 0; fi < tier.currentFeatures.length; ++fi) {
            var f = tier.currentFeatures[fi];
            if (f.id) {
                fbid[f.id] = f;
            }
        }
    };
    
    var superParentsOf = function(f) {
        // FIXME: should recur.
        var spids = [];
        if (f.parents) {
            for (var pi = 0; pi < f.parents.length; ++pi) {
                var pid = f.parents[pi];
                var p = fbid[pid];
                if (!p) {
                    continue;
                }
                // alert(p.type + ':' + p.typeCv);
                if (p.typeCv == 'SO:0000704') {
                    pushnew(spids, pid);
                }
            }
        }
        return spids;
    }


    for (var fi = 0; fi < tier.currentFeatures.length; ++fi) {
        // var f = eval('[' + miniJSONify(tier.currentFeatures[fi]) + ']')[0]; 
        var f = tier.currentFeatures[fi];
        if (f.parts) {
            continue;
        }

        if (!f.min || !f.max) {
            nonPositional.push(f);
            continue;
        }

        if (f.score && f.score != '.' && f.score != '-') {
            sc = 1.0 * f.score;
            if (!minScore || sc < minScore) {
                minScore = sc;
            }
            if (!maxScore || sc > maxScore) {
                maxScore = sc;
            }
        }

        var fGroups = [];
        var fSuperGroup = null;
        if (f.groups) {
            for (var gi = 0; gi < f.groups.length; ++gi) {
                var g = f.groups[gi];
                var gid = g.id;
                if (g.type == 'gene') {
                    // Like a super-grouper...
                    fSuperGroup = gid; 
                    groups[gid] = shallowCopy(g);
                } else if (g.type == 'translation') {
                    // have to ignore this to get sensible results from bj-e :-(.
                } else {
                    pusho(groupedFeatures, gid, f);
                    groups[gid] = shallowCopy(g);
                    fGroups.push(gid);
                }
            }
        }

        if (f.parents) {
            if (!fbid) {
                init_fbid();
            }
            for (var pi = 0; pi < f.parents.length; ++pi) {
                var pid = f.parents[pi];
                var p = fbid[pid];
                if (!p) {
                    // alert("couldn't find " + pid);
                    continue;
                }
                if (!p.parts) {
                    p.parts = [f];
                }
                pushnewo(groupedFeatures, pid, p);
                pusho(groupedFeatures, pid, f);
                
                if (!groups[pid]) {
                    groups[pid] = {
                        type: p.type,
                        id: p.id,
                        label: p.label || p.id
                    };
                }
                fGroups.push(pid);

                var sgs = superParentsOf(p);
                if (sgs.length > 0) {
                    fSuperGroup = sgs[0];
                    var sp = fbid[sgs[0]];
                    groups[sgs[0]] = {
                        type: sp.type,
                        id: sp.id,
                        label: sp.label || sp.id
                    };
                    if (!tier.dasSource.collapseSuperGroups) {
                        tier.dasSource.collapseSuperGroups = true;
                        tier.isLabelValid = false;
                    }
                }
            }   
        }

        if (fGroups.length == 0) {
            pusho(ungroupedFeatures, f.type, f);
        } else if (fSuperGroup) {
            for (var g = 0; g < fGroups.length; ++g) {
                var gid = fGroups[g];
                pushnewo(superGroups, fSuperGroup, gid);
                groupsToSupers[gid] = fSuperGroup;
            } 
        }       
    }

    tier.ungroupedFeatures = ungroupedFeatures;
    tier.groupedFeatures = groupedFeatures;
    tier.groups = groups;
    tier.superGroups = superGroups;
    tier.groupsToSupers = groupsToSupers;

    if (minScore) {
        if (minScore > 0) {
            minScore = 0;
        } else if (maxScore < 0) {
            maxScore = 0;
        }
        tier.currentFeaturesMinScore = minScore;
        tier.currentFeaturesMaxScore = maxScore;
    }
}

var clipIdSeed = 0;

function drawFeatureTier(tier)
{
    var before = Date.now();

    sortFeatures(tier);
    tier.placard = null;
    tier.isQuantitative = false;         // gets reset later if we have any HISTOGRAMs.

    var featureGroupElement = tier.viewport;
    while (featureGroupElement.childNodes.length > 0) {
        featureGroupElement.removeChild(featureGroupElement.firstChild);
    }
    featureGroupElement.appendChild(tier.background);
    drawGuidelines(tier, featureGroupElement);
        
    var lh = MIN_PADDING;
    var glyphs = [];
    var specials = false;

    // Glyphify ungrouped.
        
    for (var uft in tier.ungroupedFeatures) {
        var ufl = tier.ungroupedFeatures[uft];
        // var style = styles[uft] || styles['default'];
        var style = tier.styleForFeature(ufl[0]);   // FIXME this isn't quite right...
        if (!style) continue;
        if (style.glyph == 'LINEPLOT') {
            lh += Math.max(drawLine(featureGroupElement, ufl, style, tier, lh));
            specials = true;
        } else {
            for (var pgid = 0; pgid < ufl.length; ++pgid) {
                var f = ufl[pgid];
                if (f.parts) {  // FIXME shouldn't really be needed
                    continue;
                }
                var g = glyphForFeature(f, 0, tier.styleForFeature(f), tier);
                glyphs.push(g);
            }
        }
    }

    // Merge supergroups
    
    if (tier.dasSource.collapseSuperGroups && !tier.bumped) {
        for (var sg in tier.superGroups) {
            var sgg = tier.superGroups[sg];
            tier.groups[sg].type = tier.groups[sgg[0]].type;   // HACK to make styling easier in DAS1.6
            var featsByType = {};
            for (var g = 0; g < sgg.length; ++g) {
                var gf = tier.groupedFeatures[sgg[g]];
                for (var fi = 0; fi < gf.length; ++fi) {
                    var f = gf[fi];
                    pusho(featsByType, f.type, f);
                }

                if (tier.groups[sg] && !tier.groups[sg].links || tier.groups[sg].links.length == 0) {
                    tier.groups[sg].links = tier.groups[sgg[0]].links;
                }

                delete tier.groupedFeatures[sgg[g]];  // 'cos we don't want to render the unmerged version.
            }

            for (var t in featsByType) {
                var feats = featsByType[t];
                var template = feats[0];
                var loc = null;
                for (var fi = 0; fi < feats.length; ++fi) {
                    var f = feats[fi];
                    var fl = new Range(f.min, f.max);
                    if (!loc) {
                        loc = fl;
                    } else {
                        loc = union(loc, fl);
                    }
                }
                var mergedRanges = loc.ranges();
                for (var si = 0; si < mergedRanges.length; ++si) {
                    var r = mergedRanges[si];

                    // begin coverage-counting
                    var posCoverage = ((r.max()|0) - (r.min()|0) + 1) * sgg.length;
                    var actCoverage = 0;
                    for (var fi = 0; fi < feats.length; ++fi) {
                        var f = feats[fi];
                        if ((f.min|0) <= r.max() && (f.max|0) >= r.min()) {
                            var umin = Math.max(f.min|0, r.min());
                            var umax = Math.min(f.max|0, r.max());
                            actCoverage += (umax - umin + 1);
                        }
                    }
                    var visualWeight = ((1.0 * actCoverage) / posCoverage);
                    // end coverage-counting

                    var newf = new DASFeature();
                    for (k in template) {
                        newf[k] = template[k];
                    }
                    newf.min = r.min();
                    newf.max = r.max();
                    if (newf.label && sgg.length > 1) {
                        newf.label += ' (' + sgg.length + ' vars)';
                    }
                    newf.visualWeight = ((1.0 * actCoverage) / posCoverage);
                    pusho(tier.groupedFeatures, sg, newf);
                    // supergroups are already in tier.groups.
                }
            }

            delete tier.superGroups[sg]; // Do we want this?
        }       
    }

    // Glyphify groups.

    var gl = new Array();
    for (var gid in tier.groupedFeatures) {
        gl.push(gid);
    }
    gl.sort(function(g1, g2) {
        var d = tier.groupedFeatures[g1][0].score - tier.groupedFeatures[g2][0].score;
        if (d > 0) {
            return -1;
        } else if (d == 0) {
            return 0;
        } else {
            return 1;
        }
    });

    var groupGlyphs = {};
    for (var gx = 0; gx < gl.length; ++gx) {
        var gid = gl[gx];
        var g = glyphsForGroup(tier.groupedFeatures[gid], 0, tier.groups[gid], tier,
                               (tier.dasSource.collapseSuperGroups && !tier.bumped) ? 'collapsed_gene' : 'tent');
        if (g) {
            groupGlyphs[gid] = g;
        }
    }

    for (var sg in tier.superGroups) {
        var sgg = tier.superGroups[sg];
        var sgGlyphs = [];
        var sgMin = 10000000000;
        var sgMax = -10000000000;
        for (var sgi = 0; sgi < sgg.length; ++sgi) {
            var gg = groupGlyphs[sgg[sgi]];
            groupGlyphs[sgg[sgi]] = null;
            if (gg) {
                sgGlyphs.push(gg);
                sgMin = Math.min(sgMin, gg.min);
                sgMax = Math.max(sgMax, gg.max);
            }
        }
        for (var sgi = 0; sgi < sgGlyphs.length; ++sgi) {
            var gg = sgGlyphs[sgi];
            gg.min = sgMin;
            gg.max = sgMax;
            glyphs.push(gg);
        }
    }
    for (var g in groupGlyphs) {
        var gg = groupGlyphs[g];
        if (gg) {
            glyphs.push(gg);
        }
    }

    var unbumpedST = new DSubTier();
    var bumpedSTs = [];
    var hasBumpedFeatures = false;
    var subtierMax = tier.dasSource.subtierMax || DEFAULT_SUBTIER_MAX;
    
  GLYPH_LOOP:
    for (var i = 0; i < glyphs.length; ++i) {
        var g = glyphs[i];
        g = labelGlyph(tier, g, featureGroupElement);
        if (g.bump) {
            hasBumpedFeatures = true;
        }
        if (g.bump && (tier.bumped || tier.dasSource.collapseSuperGroups)) {       // kind-of nasty.  supergroup collapsing is different from "normal" unbumping
            for (var sti = 0; sti < bumpedSTs.length;  ++sti) {
                var st = bumpedSTs[sti];
                if (st.hasSpaceFor(g)) {
                    st.add(g);
                    continue GLYPH_LOOP;
                }
            }
            if (bumpedSTs.length >= subtierMax) {
                tier.status = 'Too many overlapping features, truncating at ' + subtierMax;
            } else {
                var st = new DSubTier();
                st.add(g);
                bumpedSTs.push(st);
            }
        } else {
            unbumpedST.add(g);
        }
    }

    tier.hasBumpedFeatures = hasBumpedFeatures;

    if (unbumpedST.glyphs.length > 0) {
        bumpedSTs = [unbumpedST].concat(bumpedSTs);
    }

    var stBoundaries = [];
    if (specials) {
        stBoundaries.push(lh);
    } 
    for (var bsi = 0; bsi < bumpedSTs.length; ++bsi) {
        var st = bumpedSTs[bsi];
        var stg = st.glyphs;
        stg = stg.sort(function(g1, g2) {
            return g1.zindex - g2.zindex;
        });

	for (var i = 0; i < stg.length; ++i) {
	    var g = stg[i];
	    if (g.glyph) {
                gypos = lh;
                if (g.height < st.height) {
                    gypos += (st.height - g.height);
                }
		g.glyph.setAttribute('transform', 'translate(0, ' + gypos + ')');
                g.glyph.setAttribute('cursor', 'pointer');
                featureGroupElement.appendChild(g.glyph);
            }
        }
        
        if (g.quant) {
            tier.isLabelValid = false;    // FIXME
            tier.isQuantitative = true;
            tier.min = g.quant.min;
            tier.max = g.quant.max;
            tier.clientMin = lh + st.height;
            tier.clientMax = lh;
        }

        lh += st.height + MIN_PADDING;
        stBoundaries.push(lh);
    }

    lh = Math.max(tier.browser.minTierHeight, lh); // for sanity's sake.
    if (stBoundaries.length < 2) {
        var bumped = false;
        var minHeight = lh;
        
        var ss = tier.stylesheet;
        if (ss) {
            var ssScale = zoomForScale(tier.browser.scale);
            for (var si = 0; si < ss.styles.length; ++si) {
                var sh = ss.styles[si];
                if (!sh.zoom || sh.zoom == ssScale) {
                    var s = sh.style;
                     if (s.bump) {
                         bumped = true;
                     }
                    if (s.height && (4.0 + s.height) > minHeight) {
                        minHeight = (4.0 + s.height);
                    }
                }
            }
            if (bumped) {
                lh = 2 * minHeight;
            }
        }
    }                   

    tier.wantedLayoutHeight = lh;
    if (!tier.layoutWasDone || tier.browser.autoSizeTiers) {
        tier.layoutHeight = lh;
        if (glyphs.length > 0 || specials) {
            tier.layoutWasDone = true;
        }
        tier.placard = null;
    } else {
        if (tier.layoutHeight != lh) {
            var spandPlacard = document.createElementNS(NS_SVG, 'g');
            var frame = document.createElementNS(NS_SVG, 'rect');
            frame.setAttribute('x', 0);
            frame.setAttribute('y', -20);
            frame.setAttribute('width', tier.browser.featurePanelWidth);
            frame.setAttribute('height', 20);
            frame.setAttribute('stroke', 'red');
            frame.setAttribute('stroke-width', 1);
            frame.setAttribute('fill', 'white');
            spandPlacard.appendChild(frame);
            var spand = document.createElementNS(NS_SVG, 'text');
            spand.setAttribute('stroke', 'none');
            spand.setAttribute('fill', 'red');
            spand.setAttribute('font-family', 'helvetica');
            spand.setAttribute('font-size', '10pt');

            if (tier.layoutHeight < lh) { 
                var dispST = 0;
                while ((tier.layoutHeight - 20) >= stBoundaries[dispST]) { // NB allowance for placard!
                    ++dispST;
                }
                spand.appendChild(document.createTextNode('Show ' + (stBoundaries.length - dispST) + ' more'));
            } else {
                spand.appendChild(document.createTextNode('Show less'));
            }
            
            spand.setAttribute('x', 80);
            spand.setAttribute('y', -6);
            spandPlacard.appendChild(spand);
            var arrow = document.createElementNS(NS_SVG, 'path');
            arrow.setAttribute('fill', 'red');
            arrow.setAttribute('stroke', 'none');
            if (tier.layoutHeight < lh) {
                arrow.setAttribute('d', 'M ' +  30 + ' ' + -16 +
                                   ' L ' + 42 + ' ' + -16 +
                                   ' L ' + 36 + ' ' + -4 + ' Z');
            } else {
                arrow.setAttribute('d', 'M ' +  30 + ' ' + -4 +
                                   ' L ' + 42 + ' ' + -4 +
                                   ' L ' + 36 + ' ' + -16 + ' Z');
            }
            spandPlacard.appendChild(arrow);
            
            spandPlacard.addEventListener('mousedown', function(ev) {
                tier.layoutHeight = tier.wantedLayoutHeight;
                tier.placard = null;
                tier.clipTier();
                tier.browser.arrangeTiers();
            }, false);

            var dismiss = document.createElementNS(NS_SVG, 'text');
            dismiss.setAttribute('stroke', 'none');
            dismiss.setAttribute('fill', 'red');
            dismiss.setAttribute('font-family', 'helvetica');
            dismiss.setAttribute('font-size', '10pt');
            dismiss.appendChild(document.createTextNode("(Auto grow-shrink)"));
            dismiss.setAttribute('x', 750);
            dismiss.setAttribute('y', -6);
            dismiss.addEventListener('mousedown', function(ev) {
                ev.preventDefault(); ev.stopPropagation();
                tier.browser.autoSizeTiers = true;
                tier.browser.refresh();
            }, false);
            spandPlacard.appendChild(dismiss);

            tier.placard = spandPlacard;
        } 
    }

    var statusMsg = tier.error || tier.status;
    if (statusMsg != null) {
        var statusPlacard = document.createElementNS(NS_SVG, 'g');
        var frame = document.createElementNS(NS_SVG, 'rect');
        frame.setAttribute('x', 0);
        frame.setAttribute('y', -20);
        frame.setAttribute('width', tier.browser.featurePanelWidth);
        frame.setAttribute('height', 20);
        frame.setAttribute('stroke', 'red');
        frame.setAttribute('stroke-width', 1);
        frame.setAttribute('fill', 'white');
        statusPlacard.appendChild(frame);
        var status = document.createElementNS(NS_SVG, 'text');
        status.setAttribute('stroke', 'none');
        status.setAttribute('fill', 'red');
        status.setAttribute('font-family', 'helvetica');
        status.setAttribute('font-size', '10pt');
        status.setAttribute('x', 25);
        status.setAttribute('y', -6);
        status.appendChild(document.createTextNode(statusMsg));

        if (tier.error) {
            var dismiss = document.createElementNS(NS_SVG, 'text');
            dismiss.setAttribute('stroke', 'none');
            dismiss.setAttribute('fill', 'red');
            dismiss.setAttribute('font-family', 'helvetica');
            dismiss.setAttribute('font-size', '10pt');
            dismiss.appendChild(document.createTextNode("(Remove track)"));
            dismiss.setAttribute('x', 800);
            dismiss.setAttribute('y', -6);
            dismiss.addEventListener('mousedown', function(ev) {
                ev.preventDefault(); ev.stopPropagation();
                // dlog('Remove');
                tier.browser.removeTier(tier);
            }, false);
            statusPlacard.appendChild(dismiss);
        }

        statusPlacard.appendChild(status);
        tier.placard = statusPlacard;
    }

    tier.clipTier();
            
    tier.scale = 1;

    var after = Date.now();
    // console.log('draw(' + tier.currentFeatures.length + ') took ' + (after-before) + 'ms');
}

DasTier.prototype.clipTier = function() {
    var featureGroupElement = this.viewport;

    this.background.setAttribute("height", this.layoutHeight);

    var clipId = 'tier_clip_' + (++clipIdSeed);
    var clip = document.createElementNS(NS_SVG, 'clipPath');
    clip.setAttribute('id', clipId);
    var clipRect = document.createElementNS(NS_SVG, 'rect');
    clipRect.setAttribute('x', -500000);
    clipRect.setAttribute('y', 0);
    clipRect.setAttribute('width', 1000000);
    clipRect.setAttribute('height', this.layoutHeight);
    clip.appendChild(clipRect);
    featureGroupElement.appendChild(clip);
    featureGroupElement.setAttribute('clip-path', 'url(#' + clipId + ')');
}

function glyphsForGroup(features, y, groupElement, tier, connectorType) {
    var scale = tier.browser.scale, origin = tier.browser.origin;
    var height=1;
    var label;
    var links = null;
    var notes = null;
    var spans = null;
    var strand = null;
    var quant = null;
    var consHeight;
    var gstyle = tier.styleForFeature(groupElement);
    

    for (var i = 0; i < features.length; ++i) {
        var feature = features[i];
        // var style = stylesheet[feature.type] || stylesheet['default'];
        var style = tier.styleForFeature(feature);
        if (!style) {
            continue;
        }
        if (style.HEIGHT) {
            if (!consHeight) {
                consHeight = style.HEIGHT|0;
            } else {
                consHeight = Math.max(consHeight, style.HEIGHT|0);
            }
        }
    }
  
    var glyphGroup = document.createElementNS(NS_SVG, 'g');
    var glyphChildren = [];
    glyphGroup.dalliance_group = groupElement;
    var featureDGlyphs = [];
    for (var i = 0; i < features.length; ++i) {
        var feature = features[i];
        if (feature.orientation && strand==null) {
            strand = feature.orientation;
        }
        if (feature.notes && notes==null) {
            notes = feature.notes;
        }
        if (feature.links && links==null) {
            links = feature.links;
        }
        // var style = stylesheet[feature.type] || stylesheet['default'];
        var style = tier.styleForFeature(feature);
        if (!style) {
            continue;
        }
        if (feature.parts) {  // FIXME shouldn't really be needed
            continue;
        }
        var glyph = glyphForFeature(feature, y, style, tier, consHeight);
        if (glyph && glyph.glyph) {
            featureDGlyphs.push(glyph);
        }
    }
    if (featureDGlyphs.length == 0) {
        return null;
    }

    featureDGlyphs = featureDGlyphs.sort(function(g1, g2) {
        return g1.zindex - g2.zindex;
    });
    
    for (var i = 0; i < featureDGlyphs.length; ++i) {
        var glyph = featureDGlyphs[i];
        glyph.glyph.dalliance_group = groupElement;
        // glyphGroup.appendChild(glyph.glyph);
        glyphChildren.push(glyph.glyph);
        var gspan = new Range(glyph.min, glyph.max);
        if (spans == null) {
            spans = gspan;
        } else {
            spans = union(spans, gspan);
        }
        height = Math.max(height, glyph.height);
        if (!label && glyph.label) {
            label = glyph.label;
        }
        if (glyph.quant) {
            quant = glyph.quant;
        }
    }

    if (spans) {
        var blockList = spans.ranges();
        for (var i = 1; i < blockList.length; ++i) {
            var lmin = ((blockList[i - 1].max() + 1 - origin) * scale);
            var lmax = (blockList[i].min() - origin) * scale;

            var path;
            if (connectorType == 'collapsed_gene') {
                path = document.createElementNS(NS_SVG, 'path');
                path.setAttribute('fill', 'none');
                path.setAttribute('stroke', 'black');
                path.setAttribute('stroke-width', '1');
                
                var hh = height/2;
                var pathops = "M " + lmin + " " + (y + hh) + " L " + lmax + " " + (y + hh);
                if (lmax - lmin > 8) {
                    var lmid = (0.5*lmax) + (0.5*lmin);
                    if (strand == '+') {
                        pathops += ' M ' + (lmid - 2) + ' ' + (y+hh-4) +
                            ' L ' + (lmid + 2) + ' ' + (y+hh) +
                            ' L ' + (lmid - 2) + ' ' + (y+hh+4); 
                    } else if (strand == '-') {
                        pathops += ' M ' + (lmid + 2) + ' ' + (y+hh-4) +
                            ' L ' + (lmid - 2) + ' ' + (y+hh) +
                            ' L ' + (lmid + 2) + ' ' + (y+hh+4); 
                    }
                }
                path.setAttribute('d', pathops);
            } else {
                path = document.createElementNS(NS_SVG, 'path');
                path.setAttribute('fill', 'none');
                path.setAttribute('stroke', 'black');
                path.setAttribute('stroke-width', '1');
                
                var vee = true;
                if (gstyle && gstyle.STYLE && gstyle.STYLE != 'hat') {
                    vee = false;
                }

                var hh;
                if (quant) {
                    hh = height;  // HACK to give ensembl-like behaviour for grouped histograms.
                } else {
                    hh = height/2;
                }
                if (vee && (strand == "+" || strand == "-")) {
                    var lmid = (lmin + lmax) / 2;
                    var lmidy = (strand == "-") ? y + 12 : y;
                    path.setAttribute("d", "M " + lmin + " " + (y + hh) + " L " + lmid + " " + lmidy + " L " + lmax + " " + (y + hh));
                } else {
                    path.setAttribute("d", "M " + lmin + " " + (y + hh) + " L " + lmax + " " + (y + hh));
                }
            }
            glyphGroup.appendChild(path);
        }
    }

    for (var i = 0; i < glyphChildren.length; ++i) {
        glyphGroup.appendChild(glyphChildren[i]);
    }

    groupElement.segment = features[0].segment;
    groupElement.min = spans.min();
    groupElement.max = spans.max();
    if (notes && (!groupElement.notes || groupElement.notes.length==0)) {
        groupElement.notes = notes;
    }

    var dg = new DGlyph(glyphGroup, spans.min(), spans.max(), height);
    dg.strand = strand;
    dg.bump = true; // grouped features always bumped.
    // alert(miniJSONify(gstyle));
    if (label || (gstyle && (gstyle.LABEL || gstyle.LABELS))) {  // HACK, LABELS should work.
        dg.label = groupElement.label || label;
        var sg = tier.groupsToSupers[groupElement.id];
        if (sg && tier.superGroups[sg]) {    // workaround case where group and supergroup IDs match.
            if (groupElement.id != tier.superGroups[sg][0]) {
                dg.label = null;
            }
        }
    }
    if (quant) {
        dg.quant = quant;
    }
    return dg;
}

function glyphForFeature(feature, y, style, tier, forceHeight)
{
    var scale = tier.browser.scale, origin = tier.browser.origin;
    var gtype = style.glyph || 'BOX';
    var glyph;

    var min = feature.min;
    var max = feature.max;
    var type = feature.type;
    var strand = feature.orientation;
    var score = feature.score;
    var label = feature.label;

    var minPos = (min - origin) * scale;
    var maxPos = ((max - origin + 1) * scale);

    var requiredHeight;
    var quant;

    if (gtype == 'HIDDEN' || feature.parts) {
        glyph = null;
    } else if (gtype == 'CROSS' || gtype == 'EX' || gtype == 'SPAN' || gtype == 'LINE' || gtype == 'DOT' || gtype == 'TRIANGLE') {
        var stroke = style.FGCOLOR || 'black';
        var fill = style.BGCOLOR || 'none';
        var height = style.HEIGHT || forceHeight || 12;
        requiredHeight = height = 1.0 * height;

        var mid = (minPos + maxPos)/2;
        var hh = height/2;

        var mark;
        var bMinPos = minPos, bMaxPos = maxPos;

        if (gtype == 'CROSS') {
            mark = document.createElementNS(NS_SVG, 'path');
            mark.setAttribute('fill', 'none');
            mark.setAttribute('stroke', stroke);
            mark.setAttribute('stroke-width', 1);
            mark.setAttribute('d', 'M ' + (mid-hh) + ' ' + (y+hh) + 
                              ' L ' + (mid+hh) + ' ' + (y+hh) + 
                              ' M ' + mid + ' ' + y +
                              ' L ' + mid + ' ' + (y+height));
            bMinPos = Math.min(minPos, mid-hh);
            bMaxPos = Math.max(maxPos, mid+hh);
        } else if (gtype == 'EX') {
            mark = document.createElementNS(NS_SVG, 'path');
            mark.setAttribute('fill', 'none');
            mark.setAttribute('stroke', stroke);
            mark.setAttribute('stroke-width', 1);
            mark.setAttribute('d', 'M ' + (mid-hh) + ' ' + (y) + 
                              ' L ' + (mid+hh) + ' ' + (y+height) + 
                              ' M ' + (mid+hh) + ' ' + (y) +
                              ' L ' + (mid-hh) + ' ' + (y+height));  
            bMinPos = Math.min(minPos, mid-hh);
            bMaxPos = Math.max(maxPos, mid+hh);
        } else if (gtype == 'SPAN') {
            mark = document.createElementNS(NS_SVG, 'path');
            mark.setAttribute('fill', 'none');
            mark.setAttribute('stroke', stroke);
            mark.setAttribute('stroke-width', 1);
            mark.setAttribute('d', 'M ' + minPos + ' ' + (y+hh) +
                              ' L ' + maxPos + ' ' + (y+hh) +
                              ' M ' + minPos + ' ' + y +
                              ' L ' + minPos + ' ' + (y + height) +
                              ' M ' + maxPos + ' ' + y +
                              ' L ' + maxPos + ' ' + (y + height));
        } else if (gtype == 'LINE') {
            var lstyle = style.STYLE || 'solid';
            mark = document.createElementNS(NS_SVG, 'path');
            mark.setAttribute('fill', 'none');
            mark.setAttribute('stroke', stroke);
            mark.setAttribute('stroke-width', 1);
            if (lstyle == 'hat') {
                var dip = 0;
                if (feature.orientation == '-') {
                    dip = height;
                }
                mark.setAttribute('d', 'M ' + minPos + ' ' + (y+hh) +
                                  ' L ' + ((maxPos + minPos) / 2) + ' ' + (y+dip) +
                                  ' L ' + maxPos + ' ' + (y+hh));
            } else {
                mark.setAttribute('d', 'M ' + minPos + ' ' + (y+hh) +
                                  ' L ' + maxPos + ' ' + (y+hh));
            }
            if (lstyle == 'dashed') {
                mark.setAttribute('stroke-dasharray', '3');
            }
        } else if (gtype == 'DOT') {
            mark = document.createElementNS(NS_SVG, 'circle');
            mark.setAttribute('fill', stroke);   // yes, really...
            mark.setAttribute('stroke', 'none');
            mark.setAttribute('cx', mid);
            mark.setAttribute('cy', (y+hh));
            mark.setAttribute('r', hh);
            bMinPos = Math.min(minPos, mid-hh);
            bMaxPos = Math.max(maxPos, mid+hh);
        }  else if (gtype == 'TRIANGLE') {
            var dir = style.DIRECTION || 'N';
            if (dir === 'FORWARD') {
                if (strand === '-') {
                    dir = 'W';
                } else {
                    dir = 'E';
                }
            } else if (dir === 'REVERSE') {
                if (strand === '-') {
                    dir = 'E';
                } else {
                    dir = 'W';
                }
            }
            var width = style.LINEWIDTH || height;
            halfHeight = 0.5 * height;
            halfWidth = 0.5 * width;
            mark = document.createElementNS(NS_SVG, 'path');
            if (dir == 'E') {
            mark.setAttribute('d', 'M ' + (mid - halfWidth) + ' ' + 0 + 
                              ' L ' + (mid - halfWidth) + ' ' + height +
                              ' L ' + (mid + halfWidth) + ' ' + halfHeight + ' Z');
            } else if (dir == 'W') {
                mark.setAttribute('d', 'M ' + (mid + halfWidth) + ' ' + 0 + 
                                  ' L ' + (mid + halfWidth) + ' ' + height +
                                  ' L ' + (mid - halfWidth) + ' ' + halfHeight + ' Z');
            } else if (dir == 'S') {
                mark.setAttribute('d', 'M ' + (mid + halfWidth) + ' ' + 0 + 
                                  ' L ' + (mid - halfWidth) + ' ' + 0 +
                                  ' L ' + mid + ' ' + height + ' Z');
            } else {
                mark.setAttribute('d', 'M ' + (mid + halfWidth) + ' ' + height + 
                                  ' L ' + (mid - halfWidth) + ' ' + height +
                                  ' L ' + mid + ' ' + 0 + ' Z');
            }
            bMinPos = Math.min(minPos, mid-halfWidth);
            bMaxPos = Math.max(maxPos, mid+halfWidth);
            mark.setAttribute('fill', stroke);
            mark.setAttribute('stroke', 'none');
        }

        glyph = document.createElementNS(NS_SVG, 'g');
        if (fill == 'none' || bMinPos < minPos || bMaxPos > maxPos) {
            var bg = document.createElementNS(NS_SVG, 'rect');
            bg.setAttribute('x', bMinPos);
            bg.setAttribute('y', y);
            bg.setAttribute('width', bMaxPos - bMinPos);
            bg.setAttribute('height', height);
            bg.setAttribute('stroke', 'none');
            bg.setAttribute('fill', 'none');
            bg.setAttribute('pointer-events', 'all');
            glyph.appendChild(bg);
        }
        if (fill != 'none') {
            var bg = document.createElementNS(NS_SVG, 'rect');
            bg.setAttribute('x', minPos);
            bg.setAttribute('y', y);
            bg.setAttribute('width', maxPos - minPos);
            bg.setAttribute('height', height);
            bg.setAttribute('stroke', 'none');
            bg.setAttribute('fill', fill);
            bg.setAttribute('pointer-events', 'all');
            glyph.appendChild(bg);
        }
        glyph.appendChild(mark);
/*
        if (bMinPos < minPos) {
            min = bMinPos/scale + origin;
        } 
        if (bMaxPos > maxPos) {
            max = (bMaxPos-1)/scale + origin;
        } */
    } else if (gtype == 'PRIMERS') {
        var arrowColor = style.FGCOLOR || 'red';
        var lineColor = style.BGCOLOR || 'black';
        var height = style.HEIGHT || forceHeight || 12;
        requiredHeight = height = 1.0 * height;

        var mid = (minPos + maxPos)/2;
        var hh = height/2;

        var glyph = document.createElementNS(NS_SVG, 'g');
        var line = document.createElementNS(NS_SVG, 'path');
        line.setAttribute('stroke', lineColor);
        line.setAttribute('fill', 'none');
        line.setAttribute('d', 'M ' + minPos + ' ' + (height/2) + ' L ' + maxPos + ' ' + (height/2));
        glyph.appendChild(line);

        var trigs = document.createElementNS(NS_SVG, 'path');
        trigs.setAttribute('stroke', 'none');
        trigs.setAttribute('fill', 'arrowColor');
        trigs.setAttribute('d', 'M ' + minPos + ' ' + 0 + ' L ' + minPos + ' ' + height + ' L ' + (minPos + height) + ' ' + (height/2) + ' Z ' +
                                'M ' + maxPos + ' ' + 0 + ' L ' + maxPos + ' ' + height + ' L ' + (maxPos - height) + ' ' + (height/2) + ' Z');
        glyph.appendChild(trigs);
    } else if (gtype == 'ARROW') {
        var parallel = style.PARALLEL ? style.PARALLEL == 'yes' : true;
        var ne = style.NORTHEAST && style.NORTHEAST == 'yes';
        var sw = style.SOUTHWEST && style.SOUTHWEST == 'yes';

        var stroke = style.FGCOLOR || 'none';
        var fill = style.BGCOLOR || 'green';
        var height = style.HEIGHT || forceHeight || 12;
        requiredHeight = height = 1.0 * height;
        var headInset = parallel ? 0.5 *height : 0.25 * height;
        var midPos = (maxPos + minPos)/2;
        var instep = parallel ? 0.25 * height : 0.4 * height;
        
        if (parallel) {
            if (ne && (maxPos - midPos < height)) {
                maxPos = midPos + height;
            }
            if (sw && (midPos - minPos < height)) {
                minPos = midPos - height;
            }
        } else {
            if (maxPos - minPos < (0.75 * height)) {
                minPos = midPos - (0.375 * height);
                maxPos = midPos + (0.375 * height);
            }
        }

        var path = document.createElementNS(NS_SVG, 'path');
        path.setAttribute('fill', fill);
        path.setAttribute('stroke', stroke);
        if (stroke != 'none') {
            path.setAttribute('stroke-width', 1);
        }

        var pathops;
        if (parallel) {
            pathops = 'M ' + midPos + ' ' + instep;
            if (ne) {
                pathops += ' L ' + (maxPos - headInset) + ' ' + instep + 
                    ' L ' + (maxPos - headInset) + ' 0' +
                    ' L ' + maxPos + ' ' + (height/2) +
                    ' L ' + (maxPos - headInset) + ' ' + height +
                    ' L ' + (maxPos - headInset) + ' ' + (height - instep);
            } else {
                pathops += ' L ' + maxPos + ' ' + instep +
                    ' L ' + maxPos + ' ' + (height - instep);
            }
            if (sw) {
                pathops += ' L ' + (minPos + headInset) + ' ' + (height-instep) +
                    ' L ' + (minPos + headInset) + ' ' + height + 
                    ' L ' + minPos + ' ' + (height/2) +
                    ' L ' + (minPos + headInset) + ' ' + ' 0' +
                    ' L ' + (minPos + headInset) + ' ' + instep;
            } else {
                pathops += ' L ' + minPos + ' ' + (height-instep) +
                    ' L ' + minPos + ' ' + instep;
            }
            pathops += ' Z';
        } else {
            pathops = 'M ' + (minPos + instep) + ' ' + (height/2);
            if (ne) {
                pathops += ' L ' + (minPos + instep) + ' ' + headInset +
                    ' L ' + minPos + ' ' + headInset +
                    ' L ' + midPos + ' 0' +
                    ' L ' + maxPos + ' ' + headInset +
                    ' L ' + (maxPos - instep) + ' ' + headInset;
            } else {
                pathops += ' L ' + (minPos + instep) + ' 0' +
                    ' L ' + (maxPos - instep) + ' 0';
            }
            if (sw) {
                pathops += ' L ' + (maxPos - instep) + ' ' + (height - headInset) +
                    ' L ' + maxPos + ' ' + (height - headInset) +
                    ' L ' + midPos + ' ' + height + 
                    ' L ' + minPos + ' ' + (height - headInset) +
                    ' L ' + (minPos + instep) + ' ' + (height - headInset);
            } else {
                pathops += ' L ' + (maxPos - instep) + ' ' + height +
                    ' L ' + (minPos + instep) + ' ' + height;
            }
            pathops += ' Z';
        }
        path.setAttribute('d', pathops);

        glyph = path;
    } else if (gtype == 'ANCHORED_ARROW') {
        var stroke = style.FGCOLOR || 'none';
        var fill = style.BGCOLOR || 'green';
        var height = style.HEIGHT || forceHeight || 12;
        requiredHeight = height = 1.0 * height;
        var lInset = 0;
        var rInset = 0;
        var minLength = height + 2;
        var instep = 0.333333 * height;
        

        if (feature.orientation) {
            if (feature.orientation == '+') {
                rInset = height/2;
            } else if (feature.orientation == '-') {
                lInset = height/2;
            }
        }

        if (maxPos - minPos < minLength) {
            minPos = (maxPos + minPos - minLength) / 2;
            maxPos = minPos + minLength;
        }

        var path = document.createElementNS("http://www.w3.org/2000/svg", "path");
        path.setAttribute("fill", fill);
        path.setAttribute('stroke', stroke);
        if (stroke != 'none') {
            path.setAttribute("stroke-width", 1);
        }
        
        path.setAttribute('d', 'M ' + ((minPos + lInset)) + ' ' + ((y+instep)) +
                          ' L ' + ((maxPos - rInset)) + ' ' + ((y+instep)) +
                          ' L ' + ((maxPos - rInset)) + ' ' + (y) +
                          ' L ' + (maxPos) + ' ' + ((y+(height/2))) +
                          ' L ' + ((maxPos - rInset)) + ' ' + ((y+height)) +
                          ' L ' + ((maxPos - rInset)) + ' ' + ((y + instep + instep)) +
                          ' L ' + ((minPos + lInset)) + ' ' + ((y + instep + instep)) +
                          ' L ' + ((minPos + lInset)) + ' ' + ((y + height)) +
                          ' L ' + (minPos) + ' ' + ((y+(height/2))) +
                          ' L ' + ((minPos + lInset)) + ' ' + (y) +
                          ' L ' + ((minPos + lInset)) + ' ' + ((y+instep)));

        glyph = path;
    } else if (gtype == 'TEXT') {
        var textFill = style.FGCOLOR || 'none';
        var bgFill = style.BGCOLOR || 'none';
        var height = style.HEIGHT || forceHeight || 12;
        var tstring = style.STRING;
        requiredHeight = height;
        if (!tstring) {
            glyph = null;
        } else {
            var txt = makeElementNS(NS_SVG, 'text', tstring, {
                stroke: 'none',
                fill: textFill
            });
            tier.viewport.appendChild(txt);
            var bbox = NULL_BBOX;
            try {
                bbox = txt.getBBox();
            } catch (e) {}
            tier.viewport.removeChild(txt);
            txt.setAttribute('x', (minPos + maxPos - bbox.width)/2);
            txt.setAttribute('y', height - 2);

            if (bgFill == 'none') {
                glyph = txt;
            } else {
                glyph = makeElementNS(NS_SVG, 'g', [
                    makeElementNS(NS_SVG, 'rect', null, {
                        x: minPos,
                        y: 0,
                        width: (maxPos - minPos),
                        height: height,
                        fill: bgFill,
                        stroke: 'none'
                    }),
                    txt]);
            }

            if (bbox.width > (maxPos - minPos)) {
                var tMinPos = (minPos + maxPos - bbox.width)/2;
                var tMaxPos = minPos + bbox.width;
                min = ((tMinPos/scale)|0) + origin;
                max = ((tMaxPos/scale)|0) + origin;
            }
        }
    } else {
        // BOX plus other rectangular stuff
        // Also handles HISTOGRAM, GRADIENT, and TOOMANY.
    
        var stroke = style.FGCOLOR || 'none';
        var fill = feature.override_color || style.BGCOLOR || style.COLOR1 || 'green';
        var height = style.HEIGHT || forceHeight || 12;
        requiredHeight = height = 1.0 * height;

        if (style.WIDTH) {
            var w = style.WIDTH|0;
            minPos = (maxPos + minPos - w) / 2;
            maxPos = minPos + w;
        } else if (maxPos - minPos < MIN_FEATURE_PX) {
            minPos = (maxPos + minPos - MIN_FEATURE_PX) / 2;
            maxPos = minPos + MIN_FEATURE_PX;
        }

        if ((gtype == 'HISTOGRAM' || gtype == 'GRADIENT') && score !== 'undefined') {
            var smin = tier.dasSource.forceMin || style.MIN || tier.currentFeaturesMinScore;
            var smax = tier.dasSource.forceMax || style.MAX || tier.currentFeaturesMaxScore;

            if (!smax) {
                if (smin < 0) {
                    smax = 0;
                } else {
                    smax = 10;
                }
            }
            if (!smin) {
                smin = 0;
            }

            if ((1.0 * score) < (1.0 *smin)) {
                score = smin;
            }
            if ((1.0 * score) > (1.0 * smax)) {
                score = smax;
            }
            var relScore = ((1.0 * score) - smin) / (smax-smin);

            if (style.COLOR2) {
                var loc, hic, frac;
                if (style.COLOR3) {
                    if (relScore < 0.5) {
                        loc = dasColourForName(style.COLOR1);
                        hic = dasColourForName(style.COLOR2);
                        frac = relScore * 2;
                    } else {
                        loc = dasColourForName(style.COLOR2);
                        hic = dasColourForName(style.COLOR3);
                        frac = (relScore * 2.0) - 1.0;
                    }
                } else {
                    loc = dasColourForName(style.COLOR1);
                    hic = dasColourForName(style.COLOR2);
                    frac = relScore;
                }

                fill = new DColour(
                    ((loc.red * (1.0 - frac)) + (hic.red * frac))|0,
                    ((loc.green * (1.0 - frac)) + (hic.green * frac))|0,
                    ((loc.blue * (1.0 - frac)) + (hic.blue * frac))|0
                ).toSvgString();
            } 

            if (gtype == 'HISTOGRAM') {
                if (true) {
                    var relOrigin = (-1.0 * smin) / (smax - smin);
                    if (relScore >= relOrigin) {
                        height = Math.max(1, (relScore - relOrigin) * requiredHeight);
                        y = y + ((1.0 - relOrigin) * requiredHeight) - height;
                    } else {
                        height = Math.max(1, (relOrigin - relScore) * requiredHeight);
                        y = y + ((1.0 - relOrigin) * requiredHeight);
                    }
                } else {
                    // old impl
                    height = relScore * height;
                    y = y + (requiredHeight - height);
                }
                
                quant = {
                    min: smin,
                    max: smax
                };
            }

            minPos -= 0.25
            maxPos += 0.25;   // Fudge factor to mitigate pixel-jitter.
        }
 
        // dlog('min=' + min + '; max=' + max + '; minPos=' + minPos + '; maxPos=' + maxPos);

        var rect = document.createElementNS(NS_SVG, 'rect');
        rect.setAttribute('x', minPos);
        rect.setAttribute('y', y);
        rect.setAttribute('width', maxPos - minPos);
        rect.setAttribute('height', height);
        rect.setAttribute('stroke', stroke);
        rect.setAttribute('stroke-width', 1);
        rect.setAttribute('fill', fill);
        
        if (feature.visualWeight && feature.visualWeight < 1.0) {
            rect.setAttribute('fill-opacity', feature.visualWeight);
            if (stroke != 'none') {
                rect.setAttribute('stroke-opacity', feature.visualWeight);
            }
        }
        
        if (gtype == 'TOOMANY') {
            var bits = [rect];
            for (var i = 3; i < height; i += 3) {
                bits.push(makeElementNS(NS_SVG, 'line', null, {
                    x1: minPos,
                    y1: i,
                    x2: maxPos,
                    y2: i,
                    stroke: stroke,
                    strokeWidth: 0.5
                }));
            }
            glyph = makeElementNS(NS_SVG, 'g', bits);
        } else if (feature.seq && scale >= 1) {
            var refSeq;
            if (tier.currentSequence) {
                refSeq = tier.currentSequence;
            } else {
            }

            var seq  = feature.seq.toUpperCase();
            var gg = [];
            for (var i = 0; i < seq.length; ++i) {
                var base = seq.substr(i, 1);
                var color = null;
                // var color = baseColors[base];
                if (refSeq && refSeq.seq && refSeq.start <= min && refSeq.end >= max) {
                    var refBase = refSeq.seq.substr((min|0) + (i|0) - (refSeq.start|0), 1).toUpperCase();
                    if (refBase !== base) {
                        color = 'red';
                    }
                }

                if (!color) {
                    color = 'gray';
                }

                if (scale >= 8) {
                    var labelText = document.createElementNS(NS_SVG, 'text');
                    labelText.setAttribute("x", minPos + i*scale);
                    labelText.setAttribute("y",  12);
                    labelText.setAttribute('stroke', 'none');
                    labelText.setAttribute('fill', color);
                    labelText.appendChild(document.createTextNode(base));
                    gg.push(labelText);
                    requiredHeight = 14;
                } else {
                    var br = document.createElementNS(NS_SVG, 'rect');
                    br.setAttribute('x', minPos + i*scale);
                    br.setAttribute('y', y);
                    br.setAttribute('height', height);
                    br.setAttribute('width', scale);
                    br.setAttribute('fill', color);
                    br.setAttribute('stroke', 'none');
                    gg.push(br);
                }
            }

            if (scale >= 8) {
                min -= 1;
                max += 1;
            } else {
                min = Math.floor(min - (1 / scale))|0;
                max = Math.ceil(max + (1/scale))|0;
            }
            
            glyph = makeElementNS(NS_SVG, 'g', gg);
        } else {
            glyph = rect;
        }
    }

    if (glyph) {
        glyph.dalliance_feature = feature;
    }
    var dg = new DGlyph(glyph, min, max, requiredHeight);

    if (isDasBooleanTrue(style.LABEL) && (feature.label || feature.id)) {
        dg.label = feature.label || feature.id;
    }
    if (isDasBooleanTrue(style.BUMP)) {
        dg.bump = true;
    }
    dg.strand = feature.orientation || '0';
    if (quant) {
        dg.quant = quant;
    }
    dg.zindex = style.ZINDEX || 0;

    return dg;
}

function isDasBooleanTrue(s) {
    s = ('' + s).toLowerCase();
    return s==='yes' || s==='true';
}

function labelGlyph(tier, dglyph, featureTier) {
    var scale = tier.browser.scale, origin = tier.browser.origin;
    if (tier.dasSource.labels !== false) {
        if (dglyph.glyph && dglyph.label) {
            var label = dglyph.label;
            var labelText = document.createElementNS(NS_SVG, 'text');
            labelText.setAttribute('x', (dglyph.min - origin) * scale);
            labelText.setAttribute('y', dglyph.height + 15);
            labelText.setAttribute('stroke-width', 0);
            labelText.setAttribute('fill', 'black');
            labelText.setAttribute('class', 'label-text');
            labelText.setAttribute('font-family', 'helvetica');
            labelText.setAttribute('font-size', '10pt');
            //mpi2 edit to remove arrows on label for direction
//            if (dglyph.strand == '+') {
//                label = label + '>';
//            } else if (dglyph.strand == '-') {
//                label = '<' + label;
//            }
            labelText.appendChild(document.createTextNode(label));

            featureTier.appendChild(labelText);
            var width = labelText.getBBox().width;
            featureTier.removeChild(labelText);

            var g;
            if (dglyph.glyph.localName == 'g') {
                g = dglyph.glyph;
            } else {
                g = document.createElementNS(NS_SVG, 'g');
                g.appendChild(dglyph.glyph);
            }
            g.appendChild(labelText);
            dglyph.glyph = g;
            dglyph.height = dglyph.height + 20;
            
            var textMax = (dglyph.min|0) + ((width + 10) / scale)
            if (textMax > dglyph.max) {
                var adj = (textMax - dglyph.max)/2;
                var nmin = ((dglyph.min - adj - origin) * scale) + 5;
                labelText.setAttribute('x', nmin)
                dglyph.min = ((nmin/scale)+origin)|0;
                dglyph.max = (textMax-adj)|0;
            } else {
                // Mark as a candidate for label-jiggling

                labelText.jiggleMin = (dglyph.min - origin) * scale;
                labelText.jiggleMax = ((dglyph.max - origin) * scale) - width;
            }
        }
    }
    return dglyph;
}
/* -*- mode: javascript; c-basic-offset: 4; indent-tabs-mode: nil -*- */

// 
// Dalliance Genome Explorer
// (c) Thomas Down 2006-2010
//
// karyoscape.js
//

function Karyoscape(browser, dsn)
{
    this.browser = browser; // for tooltips.
    this.dsn = dsn;
    this.svg = makeElementNS(NS_SVG, 'g');
    this.width = 250;
}

Karyoscape.prototype.update = function(chr, start, end) {
    this.start = start;
    this.end = end;
    if (!this.chr || chr != this.chr) {
        this.chr = chr;
        removeChildren(this.svg);

        var kscape = this;
        this.dsn.features(
            new DASSegment(chr),
            {type: 'karyotype'},
            function(karyos, err, segmentMap) {
                if (segmentMap && segmentMap[chr] && segmentMap[chr].max) {
                    kscape.chrLen = segmentMap[chr].max;
                } else {
                    kscape.chrLen = null;
                }
                kscape.karyos = karyos || [];
                kscape.redraw();
            }
        );
    } else {
        this.setThumb();
    }
}

var karyo_palette = {
    gneg: 'white',
    gpos25: 'rgb(200,200,200)',
    gpos33: 'rgb(180,180,180)',
    gpos50: 'rgb(128,128,128)',
    gpos66: 'rgb(100,100,100)',
    gpos75: 'rgb(64,64,64)',
    gpos100: 'rgb(0,0,0)',
    gpos: 'rgb(0,0,0)',
    gvar: 'rgb(100,100,100)',
    acen: 'rgb(100,100,100)',
    stalk: 'rgb(100,100,100)'
};

Karyoscape.prototype.redraw = function() {
    removeChildren(this.svg);
    this.karyos = this.karyos.sort(function(k1, k2) {
        return (k1.min|0) - (k2.min|0);
    });
    if (this.karyos.length > 0) {
        if (!this.chrLen) {
            this.chrLen = this.karyos[this.karyos.length - 1].max;
        }
    } else {
        if (!this.chrLen) {
            alert('Warning: insufficient data to set up spatial navigator');
            this.chrLen = 200000000;
        } 
        this.karyos.push({
            min: 1,
            max: this.chrLen,
            label: 'gneg'
        });
    }
    var bandspans = null;
    for (var i = 0; i < this.karyos.length; ++i) {
        var k = this.karyos[i];
        var bmin = ((1.0 * k.min) / this.chrLen) * this.width;
        var bmax = ((1.0 * k.max) / this.chrLen) * this.width;
        var col = karyo_palette[k.label];
        if (!col) {
            // alert("don't understand " + k.label);
        } else {
            if (bmax > bmin) {
                var band = makeElementNS(NS_SVG, 'rect', null, {
                    x: bmin,
                    y: (k.label == 'stalk' || k.label == 'acen' ? 5 : 0),
                    width: (bmax - bmin),
                    height: (k.label == 'stalk' || k.label == 'acen'? 5 : 15),
                    stroke: 'none',
                    fill: col
                });
                if (k.label.substring(0, 1) == 'g') {
                    var br = new Range(k.min, k.max);
                    if (bandspans == null) {
                        bandspans = br;
                    } else {
                        bandspans = union(bandspans, br);
                    }
                }
                this.browser.makeTooltip(band, k.id);
                this.svg.appendChild(band);
            }
        }
    }

    if (bandspans) {
        var r = bandspans.ranges();

        var pathopsT = 'M 0 10 L 0 0';
        var pathopsB = 'M 0 5 L 0 15';
        
        var curx = 0;
        for (var ri = 0; ri < r.length; ++ri) {
            var rr = r[ri];
            var bmin = ((1.0 * rr.min()) / this.chrLen) * this.width;
            var bmax = ((1.0 * rr.max()) / this.chrLen) * this.width;
            if ((bmin - curx > 0.75)) {
                pathopsT += ' M ' + bmin + ' 0';
                pathopsB += ' M ' + bmin + ' 15';
            }
            pathopsT +=  ' L ' + bmax + ' 0';
            pathopsB +=  ' L ' + bmax + ' 15';
            curx = bmax;
        }
        if ((this.width - curx) > 0.75) {
            pathopsT += ' M ' + this.width + ' 0';
            pathopsB += ' M ' + this.width + ' 15';
        } else {
            pathopsT += ' L ' + this.width + ' 0';
            pathopsB += ' L ' + this.width + ' 15';
        }
        pathopsT +=  ' L ' + this.width + ' 10';
        pathopsB +=  ' L ' + this.width + ' 5';
        this.svg.appendChild(makeElementNS(NS_SVG, 'path', null, {
            d: pathopsT + ' ' + pathopsB,
            stroke: 'black',
            strokeWidth: 2,
            fill: 'none'
        }));
    }

    this.thumb = makeElementNS(NS_SVG, 'rect', null, {
        x: 50, y: -5, width: 8, height: 25,
        fill: 'blue', fillOpacity: 0.5, stroke: 'none'
    });
    this.svg.appendChild(this.thumb);
    this.setThumb();

    var thisKaryo = this;
    var sliderDeltaX;

    var moveHandler = function(ev) {
        ev.stopPropagation(); ev.preventDefault();
        var sliderX = Math.max(-4, Math.min(ev.clientX + sliderDeltaX, thisKaryo.width - 4));
        thisKaryo.thumb.setAttribute('x', sliderX);
//      if (thisSlider.onchange) {
//          thisSlider.onchange(value, false);
//      }
    }
    var upHandler = function(ev) {
        ev.stopPropagation(); ev.preventDefault();
        if (thisKaryo.onchange) {
            thisKaryo.onchange((1.0 * ((thisKaryo.thumb.getAttribute('x')|0) + 4)) / thisKaryo.width, true);
        }
        document.removeEventListener('mousemove', moveHandler, true);
        document.removeEventListener('mouseup', upHandler, true);
    }

    this.thumb.addEventListener('mousedown', function(ev) {
        ev.stopPropagation(); ev.preventDefault();
        sliderDeltaX = thisKaryo.thumb.getAttribute('x') - ev.clientX;
        document.addEventListener('mousemove', moveHandler, true);
        document.addEventListener('mouseup', upHandler, true);
    }, false);
}

Karyoscape.prototype.setThumb = function() {
    var pos = ((this.start|0) + (this.end|0)) / 2
    var gpos = ((1.0 * pos)/this.chrLen) * this.width;
    if (this.thumb) {
        this.thumb.setAttribute('x', gpos - 4);
    }
}
            

/* -*- mode: javascript; c-basic-offset: 4; indent-tabs-mode: nil -*- */

// 
// Dalliance Genome Explorer
// (c) Thomas Down 2006-2011
//
// kspace.js: Manage a block of Known Space
//


function FetchPool() {
    this.reqs = [];
}

FetchPool.prototype.addRequest = function(xhr) {
    this.reqs.push(xhr);
}

FetchPool.prototype.abortAll = function() {
    for (var i = 0; i < this.reqs.length; ++i) {
        this.reqs[i].abort();
    }
}

function KSCacheBaton(chr, min, max, scale, features, status) {
    this.chr = chr;
    this.min = min;
    this.max = max;
    this.scale = scale;
    this.features = features || [];
    this.status = status;
}

KSCacheBaton.prototype.toString = function() {
    return this.chr + ":" + this.min + ".." + this.max + ";scale=" + this.scale;
}

function KnownSpace(tierMap, chr, min, max, scale, seqSource) {
    this.tierMap = tierMap;
    this.chr = chr;
    this.min = min;
    this.max = max;
    this.scale = scale;
    this.seqSource = seqSource || new DummySequenceSource();

    this.featureCache = {};
}

KnownSpace.prototype.bestCacheOverlapping = function(chr, min, max) {
    var baton = this.featureCache[this.tierMap[0]];
    if (baton) {
        return baton;
    } else {
        return null;
    }
}

KnownSpace.prototype.viewFeatures = function(chr, min, max, scale) {
    // dlog('viewFeatures(' + chr + ', ' + min + ', ' + max + ', ' + scale +')');
    if (scale != scale) {
        throw "viewFeatures called with silly scale";
    }

    if (chr != this.chr) {
        throw "Can't extend Known Space to a new chromosome";
    }
    this.min = min;
    this.max = max;
    this.scale = scale;

    if (this.pool) {
        this.pool.abortAll();
    }
    this.pool = new FetchPool();
    this.awaitedSeq = new Awaited();
    this.seqWasFetched = false;
    
    this.startFetchesForTiers(this.tierMap);
}
    
function filterFeatures(features, min, max) {
    var ff = [];
    featuresByGroup = {};

    for (var fi = 0; fi < features.length; ++fi) {
        var f = features[fi];
        if (!f.min || !f.max) {
            ff.push(f);
        } else if (f.groups && f.groups.length > 0) {
            pusho(featuresByGroup, f.groups[0].id, f);
        } else if (f.min <= max && f.max >= min) {
            ff.push(f);
        }
    }

    for (var gid in featuresByGroup) {
        var gf = featuresByGroup[gid];
        var gmin = 100000000000, gmax = -100000000000;
        for (var fi = 0; fi < gf.length; ++fi) {
            var f = gf[fi];
            gmin = Math.min(gmin, f.min);
            gmax = Math.max(gmax, f.max);
        }
        if (gmin <= max || gmax >= min) {
            for (var fi = 0; fi < gf.length; ++fi) {
                ff.push(gf[fi]);
            }
        }
    }

    return ff;
}

KnownSpace.prototype.invalidate = function(tier) {
    this.featureCache[tier] = null;
    this.startFetchesForTiers([tier]);
}

KnownSpace.prototype.startFetchesForTiers = function(tiers) {
    var thisB = this;

    var awaitedSeq = this.awaitedSeq;
    var needSeq = false;

    for (var t = 0; t < tiers.length; ++t) {
        if (this.startFetchesFor(tiers[t], awaitedSeq)) {
            needSeq = true;
        }
    }

    if (needSeq && !this.seqWasFetched) {
        this.seqWasFetched = true;
        // dlog('needSeq ' + this.chr + ':' + this.min + '..' + this.max);
        var smin = this.min, smax = this.max;

        if (this.cs) {
            if (this.cs.start <= smin && this.cs.end >= smax) {
                var cachedSeq;
                if (this.cs.start == smin && this.cs.end == smax) {
                    cachedSeq = this.cs;
                } else {
                    cachedSeq = new DASSequence(this.cs.name, smin, smax, this.cs.alphabet, 
                                                this.cs.seq.substring(smin - this.cs.start, smax + 1 - this.cs.start));
                }
                return awaitedSeq.provide(cachedSeq);
            }
        }
        
        this.seqSource.fetch(this.chr, smin, smax, this.pool, function(err, seq) {
            if (seq) {
                if (!thisB.cs || (smin <= thisB.cs.start && smax >= thisB.cs.end) || 
                    (smin >= thisB.cs.end) || (smax <= thisB.cs.start) || 
                    ((smax - smin) > (thisB.cs.end - thisB.cs.start))) 
                {
                    thisB.cs = seq;
                }
                awaitedSeq.provide(seq);
            } else {
                dlog('Noseq: ' + miniJSONify(err));
            }
        });
    } 
}

KnownSpace.prototype.startFetchesFor = function(tier, awaitedSeq) {
    var thisB = this;

    var source = tier.getSource() || new DummyFeatureSource();
    var needsSeq = tier.needsSequence(this.scale);
    var baton = thisB.featureCache[tier];
    var wantedTypes = tier.getDesiredTypes(this.scale);
    if (wantedTypes === undefined) {
//         dlog('skipping because wantedTypes is undef');
        return false;
    }
    if (baton) {
//      dlog('considering cached features: ' + baton);
    }
    if (baton && baton.chr === this.chr && baton.min <= this.min && baton.max >= this.max) {
        var cachedFeatures = baton.features;
        if (baton.min < this.min || baton.max > this.max) {
            cachedFeatures = filterFeatures(cachedFeatures, this.min, this.max);
        }
        
        // dlog('cached scale=' + baton.scale + '; wanted scale=' + thisB.scale);
//      if ((baton.scale < (thisB.scale/2) && cachedFeatures.length > 200) || (wantedTypes && wantedTypes.length == 1 && wantedTypes.indexOf('density') >= 0) ) {
//          cachedFeatures = downsample(cachedFeatures, thisB.scale);
//      }
        // dlog('Provisioning ' + tier.toString() + ' with ' + cachedFeatures.length + ' features from cache');
//      tier.viewFeatures(baton.chr, Math.max(baton.min, this.min), Math.min(baton.max, this.max), baton.scale, cachedFeatures);   // FIXME change scale if downsampling

        thisB.provision(tier, baton.chr, Math.max(baton.min, this.min), Math.min(baton.max, this.max), baton.scale, wantedTypes, cachedFeatures, baton.status, needsSeq ? awaitedSeq : null);

        var availableScales = source.getScales();
        if (baton.scale <= this.scale || !availableScales) {
//          dlog('used cached features');
            return needsSeq;
        } else {
//          dlog('used cached features (temporarily)');
        }
    }

    source.fetch(this.chr, this.min, this.max, this.scale, wantedTypes, this.pool, function(status, features, scale) {
        if (!baton || (thisB.min < baton.min) || (thisB.max > baton.max)) {         // FIXME should be merging in some cases?
            thisB.featureCache[tier] = new KSCacheBaton(thisB.chr, thisB.min, thisB.max, scale, features, status);
        }

        //if ((scale < (thisB.scale/2) && features.length > 200) || (wantedTypes && wantedTypes.length == 1 && wantedTypes.indexOf('density') >= 0) ) {
        //    features = downsample(features, thisB.scale);
        //}
        // dlog('Provisioning ' + tier.toString() + ' with fresh features');
        //tier.viewFeatures(thisB.chr, thisB.min, thisB.max, this.scale, features);
        thisB.provision(tier, thisB.chr, thisB.min, thisB.max, scale, wantedTypes, features, status, needsSeq ? awaitedSeq : null);
    });
    return needsSeq;
}

KnownSpace.prototype.provision = function(tier, chr, min, max, actualScale, wantedTypes, features, status, awaitedSeq) {
    if (status) {
        tier.updateStatus(status);
    } else {
        var mayDownsample = false;
        var src = tier.getSource();
        while (MappedFeatureSource.prototype.isPrototypeOf(src)) {
            // dlog('Skipping up...');
            src = src.source;
        }
        if (BWGFeatureSource.prototype.isPrototypeOf(src) || BAMFeatureSource.prototype.isPrototypeOf(src)) {
            mayDownsample = true;
        }
        
        // console.log('features=' + features.length + '; maybe=' + mayDownsample + '; actualScale=' + actualScale + '; thisScale=' + this.scale + '; wanted=' + wantedTypes);

        if ((actualScale < (this.scale/2) && features.length > 200) || 
            (mayDownsample && wantedTypes && wantedTypes.length == 1 && wantedTypes.indexOf('density') >= 0))
        {
            features = downsample(features, this.scale);
        }

        if (awaitedSeq) {
            awaitedSeq.await(function(seq) {
                tier.viewFeatures(chr, min, max, actualScale, features, seq);
            });
        } else {
            tier.viewFeatures(chr, min, max, actualScale, features);
        }
    }
}


function DASFeatureSource(dasSource) {
    this.dasSource = dasSource;
}

DASFeatureSource.prototype.fetch = function(chr, min, max, scale, types, pool, callback) {
    if (types && types.length == 0) {
        callback(null, [], scale);
        return;
    }

    if (!this.dasSource.uri) {
        return;
    }

    var tryMaxBins = (this.dasSource.maxbins !== false);
    var fops = {
        type: types
    };
    if (tryMaxBins) {
        fops.maxbins = 1 + (((max - min) / scale) | 0);
    }
    
    this.dasSource.features(
        new DASSegment(chr, min, max),
        fops,
        function(features, status) {
            var retScale = scale;
            if (!tryMaxBins) {
                retScale = 0.1;
            }
            callback(status, features, retScale);
        }
    );
}

function DASSequenceSource(dasSource) {
    this.dasSource = dasSource;
}


DASSequenceSource.prototype.fetch = function(chr, min, max, pool, callback) {
    this.dasSource.sequence(
        new DASSegment(chr, min, max),
        function(seqs) {
            if (seqs.length == 1) {
                return callback(null, seqs[0]);
            } else {
                return callback("Didn't get sequence");
            }
        }
    );
}

function TwoBitSequenceSource(source) {
    var thisB = this;
    this.source = source;
    this.twoBit = new Awaited();
    makeTwoBit(new URLFetchable(source.twoBitURI), function(tb, error) {
        if (error) {
            dlog(error);
        } else {
            thisB.twoBit.provide(tb);
        }
    });
}

TwoBitSequenceSource.prototype.fetch = function(chr, min, max, pool, callback) {
        this.twoBit.await(function(tb) {
            tb.fetch(chr, min, max,
                     function(seq, err) {
                         if (err) {
                             return callback(err, null);
                         } else {
                             var sequence = new DASSequence(chr, min, max, 'DNA', seq);
                             return callback(null, sequence);
                         }
                     })
        });
}


DASFeatureSource.prototype.getScales = function() {
    return [];
}

var bwg_preflights = {};

function BWGFeatureSource(bwgSource, opts) {
    var thisB = this;
    this.bwgSource = bwgSource;
    this.opts = opts || {};
    
    thisB.bwgHolder = new Awaited();

    if (this.opts.preflight) {
        var pfs = bwg_preflights[this.opts.preflight];
        if (!pfs) {
            pfs = new Awaited();
            bwg_preflights[this.opts.preflight] = pfs;

            var req = new XMLHttpRequest();
            req.onreadystatechange = function() {
                if (req.readyState == 4) {
                    if (req.status == 200) {
                        pfs.provide('success');
                    } else {
                        pfs.provide('failure');
                    }
                }
            };
            // req.setRequestHeader('cache-control', 'no-cache');    /* Doesn't work, not an allowed request header in CORS */
            req.open('get', this.opts.preflight + '?' + hex_sha1('salt' + Date.now()), true);    // Instead, ensure we always preflight a unique URI.
            if (this.opts.credentials) {
                req.withCredentials = true;
            }
            req.send('');
        }
        pfs.await(function(status) {
            if (status === 'success') {
                thisB.init();
            }
        });
    } else {
        thisB.init();
    }
}

BWGFeatureSource.prototype.init = function() {
    var thisB = this;
    var make, arg;
    if (this.bwgSource.bwgURI) {
        make = makeBwgFromURL;
        arg = this.bwgSource.bwgURI;
    } else {
        make = makeBwgFromFile;
        arg = this.bwgSource.bwgBlob;
    }

    make(arg, function(bwg) {
        thisB.bwgHolder.provide(bwg);
    }, this.opts.credentials);
}

BWGFeatureSource.prototype.fetch = function(chr, min, max, scale, types, pool, callback) {
    var thisB = this;
    this.bwgHolder.await(function(bwg) {
        if (bwg == null) {
            return callback("Can't access binary file", null, null);
        }

        // dlog('bwg: ' + bwg.name + '; want scale: ' + scale);
        var data;
        // dlog(miniJSONify(types));
        var wantDensity = !types || types.length == 0 || arrayIndexOf(types, 'density') >= 0;
/*        if (wantDensity) {
            dlog('want density; scale=' + scale);
        } */
        if (thisB.opts.clientBin) {
            wantDensity = false;
        }
        if (bwg.type == 'bigwig' || wantDensity || (typeof thisB.opts.forceReduction !== 'undefined')) {
            var zoom = -1;
            for (var z = 0; z < bwg.zoomLevels.length; ++z) {
                if (bwg.zoomLevels[z].reduction <= scale) {
                    zoom = z;
                } else {
                    break;
                }
            }
            if (typeof thisB.opts.forceReduction !== 'undefined') {
                zoom = thisB.opts.forceReduction;
            }
           // dlog('selected zoom: ' + zoom);
            if (zoom < 0) {
                data = bwg.getUnzoomedView();
            } else {
                data = bwg.getZoomedView(zoom);
            }
        } else {
            data = bwg.getUnzoomedView();
        }
        data.readWigData(chr, min, max, function(features) {
            var fs = 1000000000;
            if (bwg.type === 'bigwig') {
                var is = (max - min) / features.length / 2;
                if (is < fs) {
                    fs = is;
                }
            }
            if (thisB.opts.link) {
                for (var fi = 0; fi < features.length; ++fi) {
                    var f = features[fi];
                    if (f.label) {
                        f.links = [new DASLink('Link', thisB.opts.link.replace(/\$\$/, f.label))];
                    }
                }
            }
            callback(null, features, fs);
        });
    });
}

BWGFeatureSource.prototype.getScales = function() {
    var bwg = this.bwgHolder.res;
    if (bwg /* && bwg.type == 'bigwig' */) {
        var scales = [1];  // Can we be smarter about inferring baseline scale?
        for (var z = 0; z < bwg.zoomLevels.length; ++z) {
            scales.push(bwg.zoomLevels[z].reduction);
        }
        return scales;
    } else {
        return null;
    }
}

function BAMFeatureSource(bamSource, opts) {
    var thisB = this;
    this.bamSource = bamSource;
    this.opts = opts || {};
    this.bamHolder = new Awaited();
    
    if (this.opts.preflight) {
        var pfs = bwg_preflights[this.opts.preflight];
        if (!pfs) {
            pfs = new Awaited();
            bwg_preflights[this.opts.preflight] = pfs;

            var req = new XMLHttpRequest();
            req.onreadystatechange = function() {
                if (req.readyState == 4) {
                    if (req.status == 200) {
                        pfs.provide('success');
                    } else {
                        pfs.provide('failure');
                    }
                }
            };
            // req.setRequestHeader('cache-control', 'no-cache');    /* Doesn't work, not an allowed request header in CORS */
            req.open('get', this.opts.preflight + '?' + hex_sha1('salt' + Date.now()), true);    // Instead, ensure we always preflight a unique URI.
            if (this.opts.credentials) {
                req.withCredentials = 'true';
            }
            req.send('');
        }
        pfs.await(function(status) {
            if (status === 'success') {
                thisB.init();
            }
        });
    } else {
        thisB.init();
    }
}

BAMFeatureSource.prototype.init = function() {
    var thisB = this;
    var bamF, baiF;
    if (this.bamSource.bamBlob) {
        bamF = new BlobFetchable(this.bamSource.bamBlob);
        baiF = new BlobFetchable(this.bamSource.baiBlob);
    } else {
        bamF = new URLFetchable(this.bamSource.bamURI, {credentials: this.opts.credentials});
        baiF = new URLFetchable(this.bamSource.baiURI || (this.bamSource.bamURI + '.bai'), {credentials: this.opts.credentials});
    }
    makeBam(bamF, baiF, function(bam) {
        thisB.bamHolder.provide(bam);
    });
}

BAMFeatureSource.prototype.fetch = function(chr, min, max, scale, types, pool, callback) {
    var thisB = this;
    this.bamHolder.await(function(bam) {
        bam.fetch(chr, min, max, function(bamRecords, error) {
            if (error) {
                callback(error, null, null);
            } else {
                var features = [];
                for (var ri = 0; ri < bamRecords.length; ++ri) {
                    var r = bamRecords[ri];
                    var f = new DASFeature();
                    f.min = r.pos + 1;
                    f.max = r.pos + r.seq.length;
                    f.segment = r.segment;
                    f.type = 'bam';
                    f.id = r.readName;
                    f.notes = ['Sequence=' + r.seq, 'CIGAR=' + r.cigar, 'MQ=' + r.mq];
                    f.seq = r.seq;
                    features.push(f);
                }
                callback(null, features, 1000000000);
            }
        });
    });
}

BAMFeatureSource.prototype.getScales = function() {
    return 1000000000;
}

function MappedFeatureSource(source, mapping) {
    this.source = source;
    this.mapping = mapping;
}

MappedFeatureSource.prototype.getScales = function() {
    return this.source.getScales();
}

MappedFeatureSource.prototype.fetch = function(chr, min, max, scale, types, pool, callback) {
    var thisB = this;

    this.mapping.sourceBlocksForRange(chr, min, max, function(mseg) {
        if (mseg.length == 0) {
            callback("No mapping available for this regions", [], scale);
        } else {
            var seg = mseg[0];
            thisB.source.fetch(seg.name, seg.start, seg.end, scale, types, pool, function(status, features, fscale) {
                var mappedFeatures = [];
                if (features) {
                    for (var fi = 0; fi < features.length; ++fi) {
                        var f = features[fi];
                        var sn = f.segment;
                        if (sn.indexOf('chr') == 0) {
                            sn = sn.substr(3);
                        }
                        var mmin = thisB.mapping.mapPoint(sn, f.min);
                        var mmax = thisB.mapping.mapPoint(sn, f.max);
                        if (!mmin || !mmax || mmin.seq != mmax.seq || mmin.seq != chr) {
                            // Discard feature.
                            // dlog('discarding ' + miniJSONify(f));
                            if (f.parts && f.parts.length > 0) {    // FIXME: Ugly hack to make ASTD source map properly.
                                 mappedFeatures.push(f);
                            }
                        } else {
                            f.segment = mmin.seq;
                            f.min = mmin.pos;
                            f.max = mmax.pos;
                            if (f.min > f.max) {
                                var tmp = f.max;
                                f.max = f.min;
                                f.min = tmp;
                            }
                            if (mmin.flipped) {
                                if (f.orientation == '-') {
                                    f.orientation = '+';
                                } else if (f.orientation == '+') {
                                    f.orientation = '-';
                                }
                            }
                            mappedFeatures.push(f);
                        }
                    }
                }

                callback(status, mappedFeatures, fscale);
            });
        }
    });
}

function DummyFeatureSource() {
}

DummyFeatureSource.prototype.getScales = function() {
    return null;
}

DummyFeatureSource.prototype.fetch = function(chr, min, max, scale, types, pool, cnt) {
    return cnt(null, [], 1000000000);
}

function DummySequenceSource() {
}

DummySequenceSource.prototype.fetch = function(chr, min, max, pool, cnt) {
    return cnt(null, null);
}
/* -*- mode: javascript; c-basic-offset: 4; indent-tabs-mode: nil -*- */

// 
// Dalliance Genome Explorer
// (c) Thomas Down 2006-2010
//
// quant-config.js: configuration of quantitatively-scaled tiers
//

var VALID_BOUND_RE = new RegExp('^-?[0-9]+(\\.[0-9]+)?$');

Browser.prototype.makeQuantConfigButton = function(quantTools, tier, ypos) {
    var thisB = this;
    quantTools.addEventListener('mousedown', function(ev) {
        ev.stopPropagation(); ev.preventDefault();
        thisB.removeAllPopups();

        var form = makeElement('table');
        var minInput = makeElement('input', '', {value: tier.min});
        form.appendChild(makeElement('tr', [makeElement('td', 'Min:'), makeElement('td', minInput)]));
        var maxInput = makeElement('input', '', {value: tier.max});
        form.appendChild(makeElement('tr', [makeElement('td', 'Max:'), makeElement('td', maxInput)]));
        
        var updateButton = makeElement('div', 'Update');
        updateButton.style.backgroundColor = 'rgb(230,230,250)';
        updateButton.style.borderStyle = 'solid';
        updateButton.style.borderColor = 'blue';
        updateButton.style.borderWidth = '3px';
        updateButton.style.padding = '2px';
        updateButton.style.margin = '10px';
        updateButton.style.width = '150px';

        updateButton.addEventListener('mousedown', function(ev) {
            ev.stopPropagation(); ev.preventDefault();

            if (!VALID_BOUND_RE.test(minInput.value)) {
                alert("Don't understand " + minInput.value);
                return;
            }
            if (!VALID_BOUND_RE.test(maxInput.value)) {
                alert("Don't understand " + maxInput.value);
                return;
            }

            tier.dasSource.forceMin = minInput.value;
            tier.dasSource.forceMax = maxInput.value;
            thisB.removeAllPopups();
            tier.draw();
            thisB.storeStatus();          // write updated limits to storage.
        }, false);

        thisB.popit(ev, 'Configure: ' + tier.dasSource.name, [form, updateButton]);
    }, false);
}
/* -*- mode: javascript; c-basic-offset: 4; indent-tabs-mode: nil -*- */

// 
// Dalliance Genome Explorer
// (c) Thomas Down 2006-2010
//
// sample.js: downsampling of quantitative features
//

var __DS_SCALES = [1, 2, 5];

function ds_scale(n) {
    return __DS_SCALES[n % __DS_SCALES.length] * Math.pow(10, (n / __DS_SCALES.length)|0);
}


function DSBin(scale, min, max) {
    this.scale = scale;
    this.tot = 0;
    this.cnt = 0;
    this.hasScore = false;
    this.min = min; this.max = max;
    this.lap = 0;
    this.covered = null;
}

DSBin.prototype.score = function() {
    if (this.cnt == 0) {
        return 0;
    } else if (this.hasScore) {
        return this.tot / this.cnt;
    } else {
        return this.lap / coverage(this.covered);
    }
}

DSBin.prototype.feature = function(f) {
    if (f.score) {
        this.tot += f.score;
        this.hasScore = true
    }
    var fMin = f.min|0;
    var fMax = f.max|0;
    var lMin = Math.max(this.min, fMin);
    var lMax = Math.min(this.max, fMax);
    // dlog('f.min=' + fMin + '; f.max=' + fMax + '; lMin=' + lMin + '; lMax=' + lMax + '; lap=' + (1.0 * (lMax - lMin + 1))/(fMax - fMin + 1));
    this.lap += (1.0 * (lMax - lMin + 1));
    ++this.cnt;
    var newRange = new Range(lMin, lMax);
    if (this.covered) {
        this.covered = union(this.covered, newRange);
    } else {
        this.covered = newRange;
    }
}

function downsample(features, targetRez) {
    var beforeDS = Date.now();

    var sn = 0;
    while (ds_scale(sn + 1) < targetRez) {
        ++sn;
    }
    var scale = ds_scale(sn);

    var binTots = [];
    var maxBin = -10000000000;
    var minBin = 10000000000;
    for (var fi = 0; fi < features.length; ++fi) {
        var f = features[fi];
        if (f.groups && f.groups.length > 0) {
            // Don't downsample complex features (?)
            return features;
        }
//      if (f.score) {
            var minLap = (f.min / scale)|0;
            var maxLap = (f.max / scale)|0;
            maxBin = Math.max(maxBin, maxLap);
            minBin = Math.min(minBin, minLap);
            for (var b = minLap; b <= maxLap; ++b) {
                var bm = binTots[b];
                if (!bm) {
                    bm = new DSBin(scale, b * scale, (b + 1) * scale - 1);
                    binTots[b] = bm;
                }
                bm.feature(f);
            }
//      }
    }

    var sampledFeatures = [];
    for (var b = minBin; b <= maxBin; ++b) {
        var bm = binTots[b];
        if (bm) {
            var f = new DASFeature();
            f.segment = features[0].segment;
            f.min = (b * scale) + 1;
            f.max = (b + 1) * scale;
            f.score = bm.score();
            f.type = 'density';
            sampledFeatures.push(f);
        }
    }

    var afterDS = Date.now();
    // dlog('downsampled ' + features.length + ' -> ' + sampledFeatures.length + ' in ' + (afterDS - beforeDS) + 'ms');
    return sampledFeatures;
}
// 
// Dalliance Genome Explorer
// (c) Thomas Down 2006-2010
//
// sequence-tier.js: renderers for sequence-related data
//

var MIN_TILE = 75;
var rulerTileColors = ['black', 'white'];
var baseColors = {A: 'green', C: 'blue', G: 'black', T: 'red'};
var steps = [1,2,5];

function tileSizeForScale(scale, min)
{
    if (!min) {
        min = MIN_TILE;
    }

    function ts(p) {
        return steps[p % steps.length] * Math.pow(10, (p / steps.length)|0);
    }
    var pow = steps.length;
    while (scale * ts(pow) < min) {
        ++pow;
    }
    return ts(pow);
}

function drawGuidelines(tier, featureGroupElement)
{
    if (tier.browser.guidelineStyle != 'background') {
        return;
    }

    var tile = tileSizeForScale(tier.browser.scale, teir.browser.guidelineSpacing);
    var pos = Math.max(0, ((tier.browser.knownStart / tile)|0) * tile);

    var seqTierMax = knownEnd;
    if (tier.browser.currentSeqMax > 0 && tier.browser.currentSeqMax < tier.browser.knownEnd) {
        seqTierMax = tier.browser.currentSeqMax;
    }

    for (var glpos = pos; glpos <= seqTierMax; glpos += tile) {
        var guideline = document.createElementNS(NS_SVG, 'line');
        guideline.setAttribute('x1', (glpos - origin) * scale);
        guideline.setAttribute('y1', 0);
        guideline.setAttribute('x2', (glpos - origin) * scale);
        guideline.setAttribute('y2', 1000);
        guideline.setAttribute('stroke', 'black');
        guideline.setAttribute('stroke-opacity', 0.2);
        guideline.setAttribute('stroke-width', 1);
        featureGroupElement.appendChild(guideline);
    }
}


function drawSeqTier(tier, seq)
{
    var scale = tier.browser.scale, knownStart = tier.knownStart, knownEnd = tier.knownEnd, origin = tier.browser.origin, currentSeqMax = tier.browser.currentSeqMax;
    if (!scale) {
        return;
    }

    var featureGroupElement = tier.viewport;
    while (featureGroupElement.childNodes.length > 0) {
        featureGroupElement.removeChild(featureGroupElement.firstChild);
    }
    featureGroupElement.appendChild(tier.background);
    drawGuidelines(tier, featureGroupElement);
    
    var tile = tileSizeForScale(scale);
    var pos = Math.max(0, ((knownStart / tile)|0) * tile);

    var seqTierMax = knownEnd;
    if (currentSeqMax > 0 && currentSeqMax < knownEnd) {
        seqTierMax = currentSeqMax;
    }
        
    var height = 35;
    var drawCheckers = false;
    if (seq && seq.seq) {
        for (var i = seq.start; i <= seq.end; ++i) {
            var base = seq.seq.substr(i - seq.start, 1).toUpperCase();
            var color = baseColors[base];
            if (!color) {
                color = 'gray';
            }
            
            if (scale >= 8) {
                var labelText = document.createElementNS(NS_SVG, "text");
                labelText.setAttribute("x", ((i - origin) * scale));
                labelText.setAttribute("y",  12);
                labelText.setAttribute("stroke-width", "0");
                labelText.setAttribute("fill", color);
                labelText.setAttribute("class", "label-text");
                labelText.appendChild(document.createTextNode(base));
                featureGroupElement.appendChild(labelText);
            } else {
                var rect = document.createElementNS(NS_SVG, "rect");
                rect.setAttribute('x', ((i - origin) * scale));
                rect.setAttribute('y', 5);
                rect.setAttribute('height', 10);
                rect.setAttribute('width', scale);
                rect.setAttribute('fill', color);
                rect.setAttribute('stroke', 'none');
                featureGroupElement.appendChild(rect);
            }
        }
    } else {
        drawCheckers = true;
    }

    while (pos <= seqTierMax) {
        if (drawCheckers) {
            var rect = document.createElementNS(NS_SVG, "rect");
            rect.setAttribute('x', (pos - origin) * scale);
            rect.setAttribute('y', 8);
            rect.setAttribute('height', 3);
            var rwid = Math.min(tile, seqTierMax - pos) * scale;
            rect.setAttribute('width', rwid);
            rect.setAttribute('fill', rulerTileColors[(pos / tile) % 2]);
            rect.setAttribute('stroke-width', 1);
            featureGroupElement.appendChild(rect);
        }
        
        if ((pos / tile) % 2 == 0) {
            var fudge = 0;
            if (!drawCheckers) {
                featureGroupElement.appendChild(
                    makeElementNS(NS_SVG, 'line', null, {
                        x1: ((pos - origin) * scale),
                        y1: 15,
                        x2: ((pos - origin) * scale),
                        y2: 35,
                        stroke: 'rgb(80, 90, 150)',
                        strokeWidth: 1
                    }));
                fudge += 3;
            }

            var labelText = document.createElementNS(NS_SVG, "text");
            labelText.setAttribute("x", ((pos - origin) * scale) + fudge);
            labelText.setAttribute("y",  30);
            labelText.setAttribute("stroke-width", "0");
            labelText.setAttribute("fill", "black");
            labelText.setAttribute("class", "label-text");
            labelText.appendChild(document.createTextNode('' + pos));
            featureGroupElement.appendChild(labelText);
        }
             
        pos += tile;
    }

    tier.layoutHeight = height;
    tier.background.setAttribute("height", height);
    tier.scale = 1;
    tier.browser.arrangeTiers();
}
/*
 * A JavaScript implementation of the Secure Hash Algorithm, SHA-1, as defined
 * in FIPS 180-1
 * Version 2.2 Copyright Paul Johnston 2000 - 2009.
 * Other contributors: Greg Holt, Andrew Kepert, Ydnar, Lostinet
 * Distributed under the BSD License
 * See http://pajhome.org.uk/crypt/md5 for details.
 */

/*
 * Configurable variables. You may need to tweak these to be compatible with
 * the server-side, but the defaults work in most cases.
 */
var hexcase = 0;  /* hex output format. 0 - lowercase; 1 - uppercase        */
var b64pad  = ""; /* base-64 pad character. "=" for strict RFC compliance   */

/*
 * These are the functions you'll usually want to call
 * They take string arguments and return either hex or base-64 encoded strings
 */
function hex_sha1(s)    { return rstr2hex(rstr_sha1(str2rstr_utf8(s))); }
function b64_sha1(s)    { return rstr2b64(rstr_sha1(str2rstr_utf8(s))); }
function any_sha1(s, e) { return rstr2any(rstr_sha1(str2rstr_utf8(s)), e); }
function hex_hmac_sha1(k, d)
  { return rstr2hex(rstr_hmac_sha1(str2rstr_utf8(k), str2rstr_utf8(d))); }
function b64_hmac_sha1(k, d)
  { return rstr2b64(rstr_hmac_sha1(str2rstr_utf8(k), str2rstr_utf8(d))); }
function any_hmac_sha1(k, d, e)
  { return rstr2any(rstr_hmac_sha1(str2rstr_utf8(k), str2rstr_utf8(d)), e); }

/*
 * Perform a simple self-test to see if the VM is working
 */
function sha1_vm_test()
{
  return hex_sha1("abc").toLowerCase() == "a9993e364706816aba3e25717850c26c9cd0d89d";
}

/*
 * Calculate the SHA1 of a raw string
 */
function rstr_sha1(s)
{
  return binb2rstr(binb_sha1(rstr2binb(s), s.length * 8));
}

/*
 * Calculate the HMAC-SHA1 of a key and some data (raw strings)
 */
function rstr_hmac_sha1(key, data)
{
  var bkey = rstr2binb(key);
  if(bkey.length > 16) bkey = binb_sha1(bkey, key.length * 8);

  var ipad = Array(16), opad = Array(16);
  for(var i = 0; i < 16; i++)
  {
    ipad[i] = bkey[i] ^ 0x36363636;
    opad[i] = bkey[i] ^ 0x5C5C5C5C;
  }

  var hash = binb_sha1(ipad.concat(rstr2binb(data)), 512 + data.length * 8);
  return binb2rstr(binb_sha1(opad.concat(hash), 512 + 160));
}

/*
 * Convert a raw string to a hex string
 */
function rstr2hex(input)
{
  try { hexcase } catch(e) { hexcase=0; }
  var hex_tab = hexcase ? "0123456789ABCDEF" : "0123456789abcdef";
  var output = "";
  var x;
  for(var i = 0; i < input.length; i++)
  {
    x = input.charCodeAt(i);
    output += hex_tab.charAt((x >>> 4) & 0x0F)
           +  hex_tab.charAt( x        & 0x0F);
  }
  return output;
}

/*
 * Convert a raw string to a base-64 string
 */
function rstr2b64(input)
{
  try { b64pad } catch(e) { b64pad=''; }
  var tab = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";
  var output = "";
  var len = input.length;
  for(var i = 0; i < len; i += 3)
  {
    var triplet = (input.charCodeAt(i) << 16)
                | (i + 1 < len ? input.charCodeAt(i+1) << 8 : 0)
                | (i + 2 < len ? input.charCodeAt(i+2)      : 0);
    for(var j = 0; j < 4; j++)
    {
      if(i * 8 + j * 6 > input.length * 8) output += b64pad;
      else output += tab.charAt((triplet >>> 6*(3-j)) & 0x3F);
    }
  }
  return output;
}

/*
 * Convert a raw string to an arbitrary string encoding
 */
function rstr2any(input, encoding)
{
  var divisor = encoding.length;
  var remainders = Array();
  var i, q, x, quotient;

  /* Convert to an array of 16-bit big-endian values, forming the dividend */
  var dividend = Array(Math.ceil(input.length / 2));
  for(i = 0; i < dividend.length; i++)
  {
    dividend[i] = (input.charCodeAt(i * 2) << 8) | input.charCodeAt(i * 2 + 1);
  }

  /*
   * Repeatedly perform a long division. The binary array forms the dividend,
   * the length of the encoding is the divisor. Once computed, the quotient
   * forms the dividend for the next step. We stop when the dividend is zero.
   * All remainders are stored for later use.
   */
  while(dividend.length > 0)
  {
    quotient = Array();
    x = 0;
    for(i = 0; i < dividend.length; i++)
    {
      x = (x << 16) + dividend[i];
      q = Math.floor(x / divisor);
      x -= q * divisor;
      if(quotient.length > 0 || q > 0)
        quotient[quotient.length] = q;
    }
    remainders[remainders.length] = x;
    dividend = quotient;
  }

  /* Convert the remainders to the output string */
  var output = "";
  for(i = remainders.length - 1; i >= 0; i--)
    output += encoding.charAt(remainders[i]);

  /* Append leading zero equivalents */
  var full_length = Math.ceil(input.length * 8 /
                                    (Math.log(encoding.length) / Math.log(2)))
  for(i = output.length; i < full_length; i++)
    output = encoding[0] + output;

  return output;
}

/*
 * Encode a string as utf-8.
 * For efficiency, this assumes the input is valid utf-16.
 */
function str2rstr_utf8(input)
{
  var output = "";
  var i = -1;
  var x, y;

  while(++i < input.length)
  {
    /* Decode utf-16 surrogate pairs */
    x = input.charCodeAt(i);
    y = i + 1 < input.length ? input.charCodeAt(i + 1) : 0;
    if(0xD800 <= x && x <= 0xDBFF && 0xDC00 <= y && y <= 0xDFFF)
    {
      x = 0x10000 + ((x & 0x03FF) << 10) + (y & 0x03FF);
      i++;
    }

    /* Encode output as utf-8 */
    if(x <= 0x7F)
      output += String.fromCharCode(x);
    else if(x <= 0x7FF)
      output += String.fromCharCode(0xC0 | ((x >>> 6 ) & 0x1F),
                                    0x80 | ( x         & 0x3F));
    else if(x <= 0xFFFF)
      output += String.fromCharCode(0xE0 | ((x >>> 12) & 0x0F),
                                    0x80 | ((x >>> 6 ) & 0x3F),
                                    0x80 | ( x         & 0x3F));
    else if(x <= 0x1FFFFF)
      output += String.fromCharCode(0xF0 | ((x >>> 18) & 0x07),
                                    0x80 | ((x >>> 12) & 0x3F),
                                    0x80 | ((x >>> 6 ) & 0x3F),
                                    0x80 | ( x         & 0x3F));
  }
  return output;
}

/*
 * Encode a string as utf-16
 */
function str2rstr_utf16le(input)
{
  var output = "";
  for(var i = 0; i < input.length; i++)
    output += String.fromCharCode( input.charCodeAt(i)        & 0xFF,
                                  (input.charCodeAt(i) >>> 8) & 0xFF);
  return output;
}

function str2rstr_utf16be(input)
{
  var output = "";
  for(var i = 0; i < input.length; i++)
    output += String.fromCharCode((input.charCodeAt(i) >>> 8) & 0xFF,
                                   input.charCodeAt(i)        & 0xFF);
  return output;
}

/*
 * Convert a raw string to an array of big-endian words
 * Characters >255 have their high-byte silently ignored.
 */
function rstr2binb(input)
{
  var output = Array(input.length >> 2);
  for(var i = 0; i < output.length; i++)
    output[i] = 0;
  for(var i = 0; i < input.length * 8; i += 8)
    output[i>>5] |= (input.charCodeAt(i / 8) & 0xFF) << (24 - i % 32);
  return output;
}

/*
 * Convert an array of big-endian words to a string
 */
function binb2rstr(input)
{
  var output = "";
  for(var i = 0; i < input.length * 32; i += 8)
    output += String.fromCharCode((input[i>>5] >>> (24 - i % 32)) & 0xFF);
  return output;
}

/*
 * Calculate the SHA-1 of an array of big-endian words, and a bit length
 */
function binb_sha1(x, len)
{
  /* append padding */
  x[len >> 5] |= 0x80 << (24 - len % 32);
  x[((len + 64 >> 9) << 4) + 15] = len;

  var w = Array(80);
  var a =  1732584193;
  var b = -271733879;
  var c = -1732584194;
  var d =  271733878;
  var e = -1009589776;

  for(var i = 0; i < x.length; i += 16)
  {
    var olda = a;
    var oldb = b;
    var oldc = c;
    var oldd = d;
    var olde = e;

    for(var j = 0; j < 80; j++)
    {
      if(j < 16) w[j] = x[i + j];
      else w[j] = bit_rol(w[j-3] ^ w[j-8] ^ w[j-14] ^ w[j-16], 1);
      var t = safe_add(safe_add(bit_rol(a, 5), sha1_ft(j, b, c, d)),
                       safe_add(safe_add(e, w[j]), sha1_kt(j)));
      e = d;
      d = c;
      c = bit_rol(b, 30);
      b = a;
      a = t;
    }

    a = safe_add(a, olda);
    b = safe_add(b, oldb);
    c = safe_add(c, oldc);
    d = safe_add(d, oldd);
    e = safe_add(e, olde);
  }
  return Array(a, b, c, d, e);

}

/*
 * Perform the appropriate triplet combination function for the current
 * iteration
 */
function sha1_ft(t, b, c, d)
{
  if(t < 20) return (b & c) | ((~b) & d);
  if(t < 40) return b ^ c ^ d;
  if(t < 60) return (b & c) | (b & d) | (c & d);
  return b ^ c ^ d;
}

/*
 * Determine the appropriate additive constant for the current iteration
 */
function sha1_kt(t)
{
  return (t < 20) ?  1518500249 : (t < 40) ?  1859775393 :
         (t < 60) ? -1894007588 : -899497514;
}

/*
 * Add integers, wrapping at 2^32. This uses 16-bit operations internally
 * to work around bugs in some JS interpreters.
 */
function safe_add(x, y)
{
  var lsw = (x & 0xFFFF) + (y & 0xFFFF);
  var msw = (x >> 16) + (y >> 16) + (lsw >> 16);
  return (msw << 16) | (lsw & 0xFFFF);
}

/*
 * Bitwise rotate a 32-bit number to the left.
 */
function bit_rol(num, cnt)
{
  return (num << cnt) | (num >>> (32 - cnt));
}
// 
// Dalliance Genome Explorer
// (c) Thomas Down 2006-2010
//
// slider.js: SVG+DOM slider control
//

function DSlider(width, opts) {
    if (!opts) {
        opts = {};
    }
    this.width = width;
    this.opts = opts;

    // privates

    var value = 0;
    var thisSlider = this;
    var sliderDeltaX;

    // Create SVG

    this.svg = document.createElementNS(NS_SVG, 'g');
    this.track = document.createElementNS(NS_SVG, 'path');
    this.track.setAttribute('fill', 'grey');
    this.track.setAttribute('stroke', 'grey');
    this.track.setAttribute('stroke-width', '1');
    this.track.setAttribute('d', 'M 0 35' +
                                 ' L ' + width + ' 35' +
                                 ' L ' + width + ' 15' +
                                 ' L 0 32 Z');
    this.svg.appendChild(this.track);

    this.handle = document.createElementNS(NS_SVG, 'rect');
    this.handle.setAttribute('x', -4);
    this.handle.setAttribute('y', 10);
    this.handle.setAttribute('width', 8);
    this.handle.setAttribute('height', 30);
    this.handle.setAttribute('stroke', 'none');
    this.handle.setAttribute('fill', 'blue');
    this.handle.setAttribute('fill-opacity', 0.5);
    this.svg.appendChild(this.handle);


    this.getValue = function() {
        return value;
    }

    this.setValue = function(v) {
        if (v < 0) {
            v = 0;
        } else if (v > this.width) {
            v = this.width;
        } 
        value = v;
        this.handle.setAttribute('x', value - 4);
    }

    this.setColor = function(c) {
        this.handle.setAttribute('fill', c);
    }

    this.onchange = null;

    var moveHandler = function(ev) {
        ev.stopPropagation(); ev.preventDefault();
        var sliderX = Math.max(-4, Math.min(ev.clientX + sliderDeltaX, width - 4));
        thisSlider.handle.setAttribute('x', sliderX);
        value = sliderX + 4;
        if (thisSlider.onchange) {
            thisSlider.onchange(value, false);
        }
    }
    var upHandler = function(ev) {
        ev.stopPropagation(); ev.preventDefault();
        if (thisSlider.onchange) {
            thisSlider.onchange(value, true);
        }
        document.removeEventListener('mousemove', moveHandler, true);
        document.removeEventListener('mouseup', upHandler, true);
    }

    this.handle.addEventListener('mousedown', function(ev) {
        ev.stopPropagation(); ev.preventDefault();
        sliderDeltaX = thisSlider.handle.getAttribute('x') - ev.clientX;
        document.addEventListener('mousemove', moveHandler, true);
        document.addEventListener('mouseup', upHandler, true);
    }, false);
}/* -*- mode: javascript; c-basic-offset: 4; indent-tabs-mode: nil -*- */

// 
// Dalliance Genome Explorer
// (c) Thomas Down 2006-2010
//
// spans.js: JavaScript Intset/Location port.
//

function Range(min, max)
{
    this._min = min|0;
    this._max = max|0;
}

Range.prototype.min = function() {
    return this._min;
}

Range.prototype.max = function() {
    return this._max;
}

Range.prototype.contains = function(pos) {
    return pos >= this._min && pos <= this._max;
}

Range.prototype.isContiguous = function() {
    return true;
}

Range.prototype.ranges = function() {
    return [this];
}

Range.prototype.toString = function() {
    return '[' + this._min + '-' + this._max + ']';
}

function _Compound(ranges) {
    this._ranges = ranges;
    // assert sorted?
}

_Compound.prototype.min = function() {
    return this._ranges[0].min();
}

_Compound.prototype.max = function() {
    return this._ranges[this._ranges.length - 1].max();
}

_Compound.prototype.contains = function(pos) {
    // FIXME implement bsearch if we use this much.
    for (var s = 0; s < this._ranges.length; ++s) {
        if (this._ranges[s].contains(pos)) {
            return true;
        }
    }
    return false;
}

_Compound.prototype.isContiguous = function() {
    return this._ranges.length > 1;
}

_Compound.prototype.ranges = function() {
    return this._ranges;
}

_Compound.prototype.toString = function() {
    var s = '';
    for (var r = 0; r < this._ranges.length; ++r) {
        if (r>0) {
            s = s + ',';
        }
        s = s + this._ranges[r].toString();
    }
    return s;
}

function union(s0, s1) {
    var ranges = s0.ranges().concat(s1.ranges()).sort(rangeOrder);
    var oranges = [];
    var current = ranges[0];

    for (var i = 1; i < ranges.length; ++i) {
        var nxt = ranges[i];
        if (nxt.min() > (current.max() + 1)) {
            oranges.push(current);
            current = nxt;
        } else {
            if (nxt.max() > current.max()) {
                current = new Range(current.min(), nxt.max());
            }
        }
    }
    oranges.push(current);

    if (oranges.length == 1) {
        return oranges[0];
    } else {
        return new _Compound(oranges);
    }
}

function intersection(s0, s1) {
    var r0 = s0.ranges();
    var r1 = s1.ranges();
    var l0 = r0.length, l1 = r1.length;
    var i0 = 0, i1 = 0;
    var or = [];

    while (i0 < l0 && i1 < l1) {
        var s0 = r0[i0], s1 = r1[i1];
        var lapMin = Math.max(s0.min(), s1.min());
        var lapMax = Math.min(s0.max(), s1.max());
        if (lapMax >= lapMin) {
            or.push(new Range(lapMin, lapMax));
        }
        if (s0.max() > s1.max()) {
            ++i1;
        } else {
            ++i0;
        }
    }
    
    if (or.length == 0) {
        return null; // FIXME
    } else if (or.length == 1) {
        return or[0];
    } else {
        return new _Compound(or);
    }
}

function coverage(s) {
    var tot = 0;
    var rl = s.ranges();
    for (var ri = 0; ri < rl.length; ++ri) {
        var r = rl[ri];
        tot += (r.max() - r.min() + 1);
    }
    return tot;
}



function rangeOrder(a, b)
{
    if (a.min() < b.min()) {
        return -1;
    } else if (a.min() > b.min()) {
        return 1;
    } else if (a.max() < b.max()) {
        return -1;
    } else if (b.max() > a.max()) {
        return 1;
    } else {
        return 0;
    }
}
/* -*- mode: javascript; c-basic-offset: 4; indent-tabs-mode: nil -*- */

// 
// Dalliance Genome Explorer
// (c) Thomas Down 2006-2010
//
// tier.js: (try) to encapsulate the functionality of a browser tier.
//

var __tier_idSeed = 0;

function DasTier(browser, source, viewport, background)
{
    var thisTier = this;

    this.id = 'tier' + (++__tier_idSeed);
    this.browser = browser;
    this.dasSource = new DASSource(source);
    this.viewport = viewport;
    this.background = background;
    this.req = null;
    this.layoutHeight = 25;
    this.bumped = true; 
    if (this.dasSource.collapseSuperGroups) {
        this.bumped = false;
    }
    this.y = 0;
    this.layoutWasDone = false;

    var fs, ss;
    if (this.dasSource.bwgURI || this.dasSource.bwgBlob) {
        fs = new BWGFeatureSource(this.dasSource, {
            credentials: this.dasSource.credentials,
            preflight: this.dasSource.preflight,
            clientBin: this.dasSource.clientBin,
            forceReduction: this.dasSource.forceReduction,
            link: this.dasSource.link
        });
        this.sourceFindNextFeature = function(chr, pos, dir, callback) {
            fs.bwgHolder.res.getUnzoomedView().getFirstAdjacent(chr, pos, dir, function(res) {
                    // dlog('got a result');
                    if (res.length > 0 && res[0] != null) {
                        callback(res[0]);
                    }
                });
        };

        if (!this.dasSource.uri && !this.dasSource.stylesheet_uri) {
            fs.bwgHolder.await(function(bwg) {
                if (!bwg) {
                    // Dummy version so that an error placard gets shown.
                    thisTier.stylesheet = new DASStylesheet();
                    return  thisTier.browser.refreshTier(thisTier);
                }

                if (thisTier.dasSource.collapseSuperGroups === undefined) {
                    if (bwg.definedFieldCount == 12 && bwg.fieldCount >= 14) {
                        thisTier.dasSource.collapseSuperGroups = true;
                        thisTier.bumped = false;
                        thisTier.isLabelValid = false;
                    }
                }

                if (bwg.type == 'bigbed') {
                    thisTier.stylesheet = new DASStylesheet();
                    
                    var wigStyle = new DASStyle();
                    wigStyle.glyph = 'BOX';
                    wigStyle.FGCOLOR = 'black';
                    wigStyle.BGCOLOR = 'blue'
                    wigStyle.HEIGHT = 8;
                    wigStyle.BUMP = true;
                    wigStyle.LABEL = true;
                    wigStyle.ZINDEX = 20;
                    thisTier.stylesheet.pushStyle({type: 'bigwig'}, null, wigStyle);

                    wigStyle.glyph = 'BOX';
                    wigStyle.FGCOLOR = 'black';
                    wigStyle.BGCOLOR = 'red'
                    wigStyle.HEIGHT = 10;
                    wigStyle.BUMP = true;
                    wigStyle.ZINDEX = 20;
                    thisTier.stylesheet.pushStyle({type: 'bb-translation'}, null, wigStyle);
                    
                    var tsStyle = new DASStyle();
                    tsStyle.glyph = 'BOX';
                    tsStyle.FGCOLOR = 'black';
                    tsStyle.BGCOLOR = 'white';
                    tsStyle.HEIGHT = 10;
                    tsStyle.ZINDEX = 10;
                    tsStyle.BUMP = true;
                    tsStyle.LABEL = true;
                    thisTier.stylesheet.pushStyle({type: 'bb-transcript'}, null, tsStyle);

                    var densStyle = new DASStyle();
                    densStyle.glyph = 'HISTOGRAM';
                    densStyle.COLOR1 = 'white';
                    densStyle.COLOR2 = 'black';
                    densStyle.HEIGHT=30;
                    thisTier.stylesheet.pushStyle({type: 'density'}, null, densStyle);
                } else {
                    thisTier.stylesheet = new DASStylesheet();
                    var wigStyle = new DASStyle();
                    wigStyle.glyph = 'HISTOGRAM';
                    wigStyle.COLOR1 = 'white';
                    wigStyle.COLOR2 = 'black';
                    wigStyle.HEIGHT=30;
                    thisTier.stylesheet.pushStyle({type: 'default'}, null, wigStyle);
                }
                thisTier.browser.refreshTier(thisTier);
            });
        }
    } else if (this.dasSource.bamURI || this.dasSource.bamBlob) {
        fs = new BAMFeatureSource(this.dasSource, {
            credentials: this.dasSource.credentials,
            preflight: this.dasSource.preflight
        });

        if (!this.dasSource.uri && !this.dasSource.stylesheet_uri) {
            fs.bamHolder.await(function(bam) {
                thisTier.stylesheet = new DASStylesheet();
                
                var densStyle = new DASStyle();
                densStyle.glyph = 'HISTOGRAM';
                densStyle.COLOR1 = 'black';
                densStyle.COLOR2 = 'red';
                densStyle.HEIGHT=30;
                thisTier.stylesheet.pushStyle({type: 'density'}, 'low', densStyle);
                thisTier.stylesheet.pushStyle({type: 'density'}, 'medium', densStyle);

                var wigStyle = new DASStyle();
                wigStyle.glyph = 'BOX';
                wigStyle.FGCOLOR = 'black';
                wigStyle.BGCOLOR = 'blue'
                wigStyle.HEIGHT = 8;
                wigStyle.BUMP = true;
                wigStyle.LABEL = false;
                wigStyle.ZINDEX = 20;
                thisTier.stylesheet.pushStyle({type: 'bam'}, 'high', wigStyle);
//                thisTier.stylesheet.pushStyle({type: 'bam'}, 'medium', wigStyle);

                thisTier.browser.refreshTier(thisTier);
            });
        }
    } else if (this.dasSource.tier_type == 'sequence') {
        if (this.dasSource.twoBitURI) {
            ss = new TwoBitSequenceSource(this.dasSource);
        } else {
            ss = new DASSequenceSource(this.dasSource);
        }
    } else {
        fs = new DASFeatureSource(this.dasSource);
        var dasAdjLock = false;
        if (this.dasSource.capabilities && arrayIndexOf(this.dasSource.capabilities, 'das1:adjacent-feature') >= 0) {
            this.sourceFindNextFeature = function(chr, pos, dir, callback) {
                if (dasAdjLock) {
                    return dlog('Already looking for a next feature, be patient!');
                }
                dasAdjLock = true;
                var fops = {
                    adjacent: chr + ':' + (pos|0) + ':' + (dir > 0 ? 'F' : 'B')
                }
                var types = thisTier.getDesiredTypes(thisTier.browser.scale);
                if (types) {
                    fops.types = types;
                }
                thisTier.dasSource.features(null, fops, function(res) {
                    dasAdjLock = false;
                    if (res.length > 0 && res[0] != null) {
                        dlog('DAS adjacent seems to be working...');
                        callback(res[0]);
                    }
                });
            };
        }
    }
    
    if (this.dasSource.mapping) {
        fs = new MappedFeatureSource(fs, this.browser.chains[this.dasSource.mapping]);
    }

    this.featureSource = fs;
    this.sequenceSource = ss;
    this.setBackground();
}

DasTier.prototype.toString = function() {
    return this.id;
}

DasTier.prototype.init = function() {
    var tier = this;

    if (tier.dasSource.uri || tier.dasSource.stylesheet_uri) {
        tier.status = 'Fetching stylesheet';
        this.dasSource.stylesheet(function(stylesheet) {
            tier.stylesheet = stylesheet;
            tier.browser.refreshTier(tier);
        }, function() {
            // tier.error = 'No stylesheet';
            tier.stylesheet = new DASStylesheet();
            var defStyle = new DASStyle();
            defStyle.glyph = 'BOX';
            defStyle.BGCOLOR = 'blue';
            defStyle.FGCOLOR = 'black';
            tier.stylesheet.pushStyle({type: 'default'}, null, defStyle);
            tier.browser.refreshTier(tier);
        });
    } else if (tier.dasSource.twoBitURI) {
        tier.stylesheet = new DASStylesheet();
        var defStyle = new DASStyle();
        defStyle.glyph = 'BOX';
        defStyle.BGCOLOR = 'blue';
        defStyle.FGCOLOR = 'black';
        tier.stylesheet.pushStyle({type: 'default'}, null, defStyle);
        tier.browser.refreshTier(tier);
    };
}

DasTier.prototype.styles = function(scale) {
    // alert('Old SS code called');
    if (this.stylesheet == null) {
        return null;
    } else if (this.browser.scale > 0.2) {
        return this.stylesheet.highZoomStyles;
    } else if (this.browser.scale > 0.01) {
        return this.stylesheet.mediumZoomStyles;
    } else {
        return this.stylesheet.lowZoomStyles;
    }
}

DasTier.prototype.getSource = function() {
    return this.featureSource;
}

DasTier.prototype.getDesiredTypes = function(scale) {
    var fetchTypes = [];
    var inclusive = false;
    var ssScale = zoomForScale(this.browser.scale);

    if (this.stylesheet) {
        // dlog('ss = ' + miniJSONify(this.stylesheet));
        var ss = this.stylesheet.styles;
        for (var si = 0; si < ss.length; ++si) {
            var sh = ss[si];
            if (!sh.zoom || sh.zoom == ssScale) {
                if (!sh.type || sh.type == 'default') {
                    inclusive = true;
                    break;
                } else {
                    pushnew(fetchTypes, sh.type);
                }
            }
        }
    } else {
        // inclusive = true;
        return undefined;
    }

    if (inclusive) {
        return null;
    } else {
        return fetchTypes;
    }
}

DasTier.prototype.needsSequence = function(scale ) {
    if (this.dasSource.tier_type === 'sequence' && scale < 5) {
        return true;
    } else if ((this.dasSource.bamURI || this.dasSource.bamBlob) && scale < 20) {
        return true
    }
    return false;
}

DasTier.prototype.setStatus = function(status) {
    dlog(status);
}

DasTier.prototype.viewFeatures = function(chr, min, max, scale, features, sequence) {
    this.currentFeatures = features;
    this.currentSequence = sequence;
    
    this.knownChr = chr;
    this.knownStart = min; this.knownEnd = max;
    this.status = null; this.error = null;

    this.setBackground();
    this.draw();
}

DasTier.prototype.updateStatus = function(status) {
    if (status) {
        this.currentFeatures = [];
        this.currentSequence = null;
        this.error = status;
    }
    this.setBackground();
    this.draw();
}

DasTier.prototype.draw = function() {
    var features = this.currentFeatures;
    var seq = this.currentSequence;
    if (this.dasSource.tier_type === 'sequence') {
        drawSeqTier(this, seq); 
    } else {
        drawFeatureTier(this);
    }
    this.originHaxx = 0;
    this.browser.arrangeTiers();
}

function zoomForScale(scale) {
    var ssScale;
    if (scale > 0.2) {
        ssScale = 'high';
    } else if (scale > 0.01) {
        ssScale = 'medium';
    } else  {
        ssScale = 'low';
    }
    return ssScale;
}


DasTier.prototype.setBackground = function() {            
//    if (this.knownStart) {

    var ks = this.knownStart || -100000000;
    var ke = this.knownEnd || -100000001;
        this.background.setAttribute('x', (ks - this.browser.origin) * this.browser.scale);
        this.background.setAttribute('width', (ke - this.knownStart + 1) * this.browser.scale);
//    }    
}

DasTier.prototype.sourceFindNextFeature = function(chr, pos, dir, callback) {
    callback(null);
}

DasTier.prototype.findNextFeature = function(chr, pos, dir, fedge, callback) {
    if (this.knownStart && pos >= this.knownStart && pos <= this.knownEnd) {
        if (this.currentFeatures) {
            var bestFeature = null;
            for (var fi = 0; fi < this.currentFeatures.length; ++fi) {
                var f = this.currentFeatures[fi];
                if (!f.min || !f.max) {
                    continue;
                }
                if (f.parents && f.parents.length > 0) {
                    continue;
                }
                if (dir < 0) {
                    if (fedge == 1 && f.max >= pos && f.min < pos) {
                        if (!bestFeature || f.min > bestFeature.min ||
                            (f.min == bestFeature.min && f.max < bestFeature.max)) {
                            bestFeature = f;
                        }
                    } else if (f.max < pos) {
                        if (!bestFeature || f.max > bestFeature.max || 
                            (f.max == bestFeature.max && f.min < bestFeature.min) ||
                            (f.min == bestFeature.mmin && bestFeature.max >= pos)) {
                            bestFeature = f;
                        } 
                    }
                } else {
                    if (fedge == 1 && f.min <= pos && f.max > pos) {
                        if (!bestFeature || f.max < bestFeature.max ||
                            (f.max == bestFeature.max && f.min > bestFeature.min)) {
                            bestFeature = f;
                        }
                    } else if (f.min > pos) {
                        if (!bestFeature || f.min < bestFeature.min ||
                            (f.min == bestFeature.min && f.max > bestFeature.max) ||
                            (f.max == bestFeature.max && bestFeature.min <= pos)) {
                            bestFeature = f;
                        }
                    }
                }
            }
            if (bestFeature) {
//                dlog('bestFeature = ' + miniJSONify(bestFeature));
                return callback(bestFeature);
            }
            if (dir < 0) {
                pos = this.knownStart;
            } else {
                pos = this.knownEnd;
            }
        }
    }
//    dlog('delegating to source: ' + pos);
    this.sourceFindNextFeature(chr, pos, dir, callback);
}
/* -*- mode: javascript; c-basic-offset: 4; indent-tabs-mode: nil -*- */

// 
// Dalliance Genome Explorer
// (c) Thomas Down 2006-2010
//
// track-adder.js
//

Browser.prototype.currentlyActive = function(source) {
    for (var i = 0; i < this.tiers.length; ++i) {
        var ts = this.tiers[i].dasSource;
        if (ts.uri == source.uri || ts.uri == source.uri + '/') {
            // Special cases where we might meaningfully want two tiers of the same URI.
            if (ts.tier_type) {
                if (!source.tier_type || source.tier_type != ts.tier_type) {
                    continue;
                }
            }
            if (ts.stylesheet_uri) {
                if (!source.stylesheet_uri || source.stylesheet_uri != ts.stylesheet_uri) {
                    continue;
                }
            }

            return true;
        }
    }
    return false;
}

Browser.prototype.makeButton = function(name, tooltip) {
    var regButton = makeElement('span', name);
    regButton.style.backgroundColor = 'rgb(230,230,250)';
    regButton.style.borderStyle = 'solid';
    regButton.style.borderColor = 'red';
    regButton.style.borderWidth = '3px';
    regButton.style.padding = '4px';
    regButton.style.marginLeft = '10px';
    regButton.style.marginRight = '10px';
    // regButton.style.width = '100px';
    regButton.style['float'] = 'left';
    if (tooltip) {
        this.makeTooltip(regButton, tooltip);
    }
    return regButton;
}

function activateButton(addModeButtons, which) {
    for (var i = 0; i < addModeButtons.length; ++i) {
        var b = addModeButtons[i];
        b.style.borderColor = (b == which) ? 'red' : 'blue';
    }
}

Browser.prototype.showTrackAdder = function(ev) {
    var thisB = this;
    var mx =  ev.clientX, my = ev.clientY;
    mx +=  document.documentElement.scrollLeft || document.body.scrollLeft;
    my +=  document.documentElement.scrollTop || document.body.scrollTop;

    var popup = document.createElement('div');
    popup.appendChild(makeElement('div', null, {}, {clear: 'both', height: '10px'})); // HACK only way I've found of adding appropriate spacing in Gecko.

    var addModeButtons = [];
    var makeStab, makeStabObserver;
    var regButton = this.makeButton('Registry', 'Browse compatible datasources from the DAS registry');
    addModeButtons.push(regButton);
    for (var m in this.mappableSources) {
        var mf  = function(mm) {
            var mapButton = thisB.makeButton(thisB.chains[mm].srcTag, 'Browse datasources mapped from ' + thisB.chains[mm].srcTag);
            addModeButtons.push(mapButton);
            mapButton.addEventListener('mousedown', function(ev) {
                ev.preventDefault(); ev.stopPropagation();
                activateButton(addModeButtons, mapButton);
                makeStab(thisB.mappableSources[mm], mm);
            }, false);
        }; mf(m);
    }
    var defButton = this.makeButton('Defaults', 'Browse the default set of data for this browser');
    addModeButtons.push(defButton);
    var custButton = this.makeButton('Custom', 'Add arbitrary DAS data');
    addModeButtons.push(custButton);
    var binButton = this.makeButton('Binary', 'Add data in bigwig or bigbed format');
    addModeButtons.push(binButton);
    activateButton(addModeButtons, regButton);
    popup.appendChild(makeElement('div', addModeButtons), null);
    
    popup.appendChild(makeElement('div', null, {}, {clear: 'both', height: '10px'})); // HACK only way I've found of adding appropriate spacing in Gecko.
    
    var addButtons = [];
    var custURL, custName, custCS, custQuant, custFile, custUser, custPass;
    var customMode = false;
    var dataToFinalize = null;

    var asform = makeElement('form', null, {}, {clear: 'both'});
    asform.addEventListener('submit', function(ev) {
            ev.stopPropagation(); ev.preventDefault();
            doAdd();
            return false;
    }, true); 
    var stabHolder = document.createElement('div');
    stabHolder.style.position = 'relative';
    stabHolder.style.overflow = 'auto';
    stabHolder.style.height = '400px';
    asform.appendChild(stabHolder);

    var __mapping;
    var __sourceHolder;


    makeStab = function(msources, mapping) {
        refreshButton.style.visibility = 'visible';
        if (__sourceHolder) {
            __sourceHolder.removeListener(makeStabObserver);
        }
        __mapping = mapping;
        __sourceHolder = msources;
        __sourceHolder.addListenerAndFire(makeStabObserver);
       
    }

    makeStabObserver = function(msources) {
        customMode = false;
        addButtons = [];
        removeChildren(stabHolder);
        if (!msources) {
            stabHolder.appendChild(makeElement('p', 'Dalliance was unable to retrieve data source information from the DAS registry, please try again later'));
            return;
        }
        var stab = document.createElement('table');
        stab.style.width='100%';
        var idx = 0;

        var sources = [];
        for (var i = 0; i < msources.length; ++i) {
            sources.push(msources[i]);
        }
        
        sources.sort(function(a, b) {
            return a.name.toLowerCase().trim().localeCompare(b.name.toLowerCase().trim());
        });

        for (var i = 0; i < sources.length; ++i) {
            var source = sources[i];
            var r = document.createElement('tr');
            r.style.backgroundColor = thisB.tierBackgroundColors[idx % thisB.tierBackgroundColors.length];

            var bd = document.createElement('td');
            bd.style.textAlign = 'center';
            if (thisB.currentlyActive(source)) {
                bd.appendChild(document.createTextNode('X'));
                thisB.makeTooltip(bd, "This data source is already active.");
            } else if (!source.props || source.props.cors) {
                var b = document.createElement('input');
                b.type = 'checkbox';
                b.dalliance_source = source;
                if (__mapping) {
                    b.dalliance_mapping = __mapping;
                }
                bd.appendChild(b);
                addButtons.push(b);
                thisB.makeTooltip(bd, "Check here then click 'Add' to activate.");
            } else {
                bd.appendChild(document.createTextNode('!'));
                thisB.makeTooltip(bd, makeElement('span', ["This data source isn't accessible because it doesn't support ", makeElement('a', "CORS", {href: 'http://www.w3.org/TR/cors/'}), "."]));
            }
            r.appendChild(bd);
            var ld = document.createElement('td');
            ld.appendChild(document.createTextNode(source.name));
            if (source.desc && source.desc.length > 0) {
                thisB.makeTooltip(ld, source.desc);
            }
            r.appendChild(ld);
            stab.appendChild(r);
            ++idx;
        }
        stabHolder.appendChild(stab);
    };
    

    regButton.addEventListener('mousedown', function(ev) {
        ev.preventDefault(); ev.stopPropagation();
        activateButton(addModeButtons, regButton);
        makeStab(thisB.availableSources);
    }, false);
    defButton.addEventListener('mousedown', function(ev) {
        ev.preventDefault(); ev.stopPropagation();
        activateButton(addModeButtons, defButton);
        makeStab(new Observed(thisB.defaultSources));
    }, false);

    binButton.addEventListener('mousedown', function(ev) {
        ev.preventDefault(); ev.stopPropagation();
        activateButton(addModeButtons, binButton);
        switchToBinMode();
    }, false);


    function switchToBinMode() {
        customMode = 'bin';
        refreshButton.style.visibility = 'hidden';

        removeChildren(stabHolder);

        if (thisB.supportsBinary) {
            stabHolder.appendChild(makeElement('h2', 'Add custom URL-based data'));
            stabHolder.appendChild(makeElement('p', ['You can add indexed binary data hosted on an web server that supports CORS (', makeElement('a', 'full details', {href: 'http://www.biodalliance.org/bin.html'}), ').  Currently supported formats are bigwig, bigbed, and indexed BAM.']));

            stabHolder.appendChild(makeElement('br'));
            stabHolder.appendChild(document.createTextNode('URL: '));
            custURL = makeElement('input', '', {size: 80, value: 'http://www.biodalliance.org/datasets/ensGene.bb'});
            stabHolder.appendChild(custURL);
            custURL.focus();
            stabHolder.appendChild(makeElement('br'));
            stabHolder.appendChild(makeElement('b', '- or -'));
            stabHolder.appendChild(makeElement('br'));
            stabHolder.appendChild(document.createTextNode('File: '));
            custFile = makeElement('input', null, {type: 'file'});
            stabHolder.appendChild(custFile);
            

            stabHolder.appendChild(makeElement('p', 'Clicking the "Add" button below will initiate a series of test queries.'));
        } else {
            stabHolder.appendChild(makeElement('h2', 'Your browser does not support binary data'));
            stabHolder.appendChild(makeElement('p', 'Browsers currently known to support this feature include Google Chrome 9 or later and Mozilla Firefox 4 or later.'));
        }
        
    }

    custButton.addEventListener('mousedown', function(ev) {
        ev.preventDefault(); ev.stopPropagation();
        activateButton(addModeButtons, custButton);
        switchToCustomMode();
    }, false);

    var switchToCustomMode = function() {
        customMode = 'das';
        refreshButton.style.visibility = 'hidden';

        removeChildren(stabHolder);

        var customForm = makeElement('div');
        customForm.appendChild(makeElement('h2', 'Add custom DAS data'));
        customForm.appendChild(makeElement('p', 'This interface is intended for adding custom or lab-specific data.  Public data can be added more easily via the registry interface.'));
                
        customForm.appendChild(document.createTextNode('URL: '));
        customForm.appendChild(makeElement('br'));
        custURL = makeElement('input', '', {size: 80, value: 'http://www.derkholm.net:8080/das/medipseq_reads/'});
        customForm.appendChild(custURL);

        customForm.appendChild(makeElement('p', 'Clicking the "Add" button below will initiate a series of test queries.  If the source is password-protected, you may be prompted to enter credentials.'));
        stabHolder.appendChild(customForm);

        custURL.focus();
    }



    var addButton = document.createElement('span');
    addButton.style.backgroundColor = 'rgb(230,230,250)';
    addButton.style.borderStyle = 'solid';
    addButton.style.borderColor = 'blue';
    addButton.style.borderWidth = '3px';
    addButton.style.padding = '2px';
    addButton.style.margin = '10px';
    addButton.style.width = '150px';
    // addButton.style.float = 'left';
    addButton.appendChild(document.createTextNode('Add'));
    addButton.addEventListener('mousedown', function(ev) {
        ev.stopPropagation(); ev.preventDefault();
        doAdd();
    }, false);

    function doAdd() {
        if (customMode) {
            if (customMode === 'das') {
                var curi = custURL.value.trim();
                if (!/^.+:\/\//.exec(curi)) {
                    curi = 'http://' + curi;
                }
                var nds = new DASSource({name: 'temporary', uri: curi});
                tryAddDAS(nds);
            } else if (customMode === 'bin') {
                var opts = {name: 'temporary'};
                var fileList = custFile.files;
                if (fileList && fileList.length > 0 && fileList[0]) {
                    opts.bwgBlob = fileList[0];
                    opts.noPersist = true;
                } else {
                    var curi = custURL.value.trim();
                    if (!/^.+:\/\//.exec(curi)) {
                        curi = 'http://' + curi;
                    }
                    opts.bwgURI = curi;
                }
                var nds = new DASSource(opts);
                tryAddBin(nds);
            } else if (customMode === 'reset') {
                switchToCustomMode();
            } else if (customMode === 'reset-bin') {
                switchToBinMode(); 
            } else if (customMode === 'prompt-bai') {
                var fileList = custFile.files;
                if (fileList && fileList.length > 0 && fileList[0]) {
                    dataToFinalize.baiBlob = fileList[0];
                    completeBAM(dataToFinalize);
                } else {
                    promptForBAI(dataToFinalize);
                }
            } else if (customMode === 'finalize') {
                dataToFinalize.name = custName.value;
                var m = custCS.value;
                if (m != '__default__') {
                    dataToFinalize.mapping = m;
                } else {
                    dataToFinalize.mapping = undefined;
                }
                if (custQuant) {
                    dataToFinalize.maxbins = custQuant.checked;
                }

                if (custUser.value.length > 1 && custPass.value.length > 1) {
                    dlog('password');
                    dataToFinalize.xUser = custUser.value;
                    dataToFinalize.xPass = custPass.value;
                }

                thisB.sources.push(dataToFinalize);
                thisB.makeTier(dataToFinalize);
                thisB.storeStatus();
                thisB.removeAllPopups();
            }
        } else {
            for (var bi = 0; bi < addButtons.length; ++bi) {
                var b = addButtons[bi];
                if (b.checked) {
                    var nds = b.dalliance_source;
                    thisB.sources.push(nds);
                    thisB.makeTier(nds);
                    thisB.storeStatus();
                }
            }
            thisB.removeAllPopups();
        }
    };

    var tryAddDAS = function(nds, retry) {
        var knownSpace = thisB.knownSpace;
        if (!knownSpace) {
            alert("Can't confirm track-addition to an uninit browser.");
            return;
        }
        var tsm = Math.max(knownSpace.min, (knownSpace.min + knownSpace.max - 100) / 2)|0;
        var testSegment = new DASSegment(knownSpace.chr, tsm, Math.min(tsm + 99, knownSpace.max));
//        dlog('test segment: ' + testSegment);
        nds.features(testSegment, {}, function(features, status) {
            // dlog('status=' + status);
            if (status) {
                if (!retry) {
                    dlog('retrying with credentials');
                    nds.credentials = true;
                    tryAddDAS(nds, true);
                } else {
                    removeChildren(stabHolder);
                    stabHolder.appendChild(makeElement('h2', 'Custom data not found'));
                    stabHolder.appendChild(makeElement('p', 'DAS uri: ' + nds.uri + ' is not answering features requests'));
                    customMode = 'reset';
                    return;
                }
            } else {
                var nameExtractPattern = new RegExp('/([^/]+)/?$');
                var match = nameExtractPattern.exec(nds.uri);
                if (match) {
                    nds.name = match[1];
                }

                tryAddDASxSources(nds);
                return;
            }
        });
    }

    function tryAddDASxSources(nds, retry) {
        var uri = nds.uri;
        if (retry) {
            var match = /(.+)\/[^\/]+\/?/.exec(uri);
            if (match) {
                uri = match[1] + '/sources';
            }
        }
//        dlog('sourceQuery: ' + uri);
        function sqfail() {
            if (!retry) {
                return tryAddDASxSources(nds, true);
            } else {
                return addDasCompletionPage(nds);
            }
        }
        new DASRegistry(uri, {credentials: nds.credentials}).sources(
            function(sources) {
                if (!sources || sources.length == 0) {
                    return sqfail();
                } 
//                dlog('got ' + sources.length + ' sources');

                var fs = null;
                if (sources.length == 1) {
                    fs = sources[0];
                } else {
                    for (var i = 0; i < sources.length; ++i) {
                        if (sources[i].uri === nds.uri) {
//                            dlog('got match!');
                            fs = sources[i];
                            break;
                        }
                    }
                }

                var coordsDetermined = false, quantDetermined = false;
                if (fs) {
                    nds.name = fs.name;
                    nds.desc = fs.desc;
                    if (fs.maxbins) {
                        nds.maxbins = true;
                    } else {
                        nds.maxbins = false;
                    }
                    if (fs.capabilities) {
                        nds.capabilities = fs.capabilities;
                    }
                    quantDetermined = true
                    
                    if (fs.coords && fs.coords.length == 1) {
                        var coords = fs.coords[0];
                        if (coordsMatch(coords, thisB.coordSystem)) {
                            coordsDetermined = true;
                        } else if (thisB.chains) {
                            for (var k in thisB.chains) {
                                if (coordsMatch(coords, thisB.chains[k].coords)) {
                                    nds.mapping = k;
                                    coordsDetermined = true;
                                }
                            }
                        }
                    }
                    
                }
                return addDasCompletionPage(nds, coordsDetermined, quantDetermined);
            },
            function() {
                return sqfail();
            }
        );
    }

    var tryAddBin = function(nds) {
        var fetchable;
        if (nds.bwgURI) {
            fetchable = new URLFetchable(nds.bwgURI);
        } else {
            fetchable = new BlobFetchable(nds.bwgBlob);
        }

        fetchable.slice(0, 1<<16).fetch(function(result, error) {
            if (!result) {
                removeChildren(stabHolder);
                stabHolder.appendChild(makeElement('h2', 'Custom data not found'));
                if (nds.bwgURI) {
                    stabHolder.appendChild(makeElement('p', 'Data URI: ' + nds.bwgURI + ' is not accessible.'));
                } else {
                    stabHolder.appendChild(makeElement('p', 'File access failed, are you using an up-to-date browser?'));
                }

                if (error) {
                    stabHolder.appendChild(makeElement('p', '' + error));
                }
                stabHolder.appendChild(makeElement('p', 'If in doubt, please check that the server where the file is hosted supports CORS.'));
                customMode = 'reset-bin';
                return;
            }

            var ba = new Uint8Array(result);
            var magic = readInt(ba, 0);
            if (magic == BIG_WIG_MAGIC || magic == BIG_BED_MAGIC) {
                var nameExtractPattern = new RegExp('/?([^/]+?)(.bw|.bb|.bigWig|.bigBed)?$');
                var match = nameExtractPattern.exec(nds.bwgURI || nds.bwgBlob.name);
                if (match) {
                    nds.name = match[1];
                }

                return addDasCompletionPage(nds, false, false, true);
            } else {
                if (ba[0] != 31 || ba[1] != 139) {
                    return binFormatErrorPage();
                }
                var unc = unbgzf(result);
                var uncba = new Uint8Array(unc);
                magic = readInt(uncba, 0);
                if (magic == BAM_MAGIC) {
                    if (nds.bwgBlob) {
                        return promptForBAI(nds);
                    } else {
                        return completeBAM(nds);
                    }
                } else {
                    // maybe Tabix?
                   return binFormatErrorPage();
                }
            }
        });
    }

    function promptForBAI(nds) {
        removeChildren(stabHolder);
        customMode = 'prompt-bai'
        stabHolder.appendChild(makeElement('h2', 'Select an index file'));
        stabHolder.appendChild(makeElement('p', 'Dalliance requires a BAM index (.bai) file when displaying BAM data.  These normally accompany BAM files.  For security reasons, web applications like Dalliance can only access local files which you have explicity selected.  Please use the file chooser below to select the appropriate BAI file'));

        stabHolder.appendChild(document.createTextNode('Index file: '));
        custFile = makeElement('input', null, {type: 'file'});
        stabHolder.appendChild(custFile);
        dataToFinalize = nds;
    }

    function completeBAM(nds) {
        var indexF;
        if (nds.baiBlob) {
            indexF = new BlobFetchable(nds.baiBlob);
        } else {
            indexF = new URLFetchable(nds.bwgURI + '.bai');
        }
        indexF.slice(0, 256).fetch(function(r) {
                var hasBAI = false;
                if (r) {
                    var ba = new Uint8Array(r);
                    var magic2 = readInt(ba, 0);
                    hasBAI = (magic2 == BAI_MAGIC);
                }
                if (hasBAI) {
                    var nameExtractPattern = new RegExp('/?([^/]+?)(.bam)?$');
                    var match = nameExtractPattern.exec(nds.bwgURI || nds.bwgBlob.name);
                    if (match) {
                        nds.name = match[1];
                    }

                    nds.bamURI = nds.bwgURI;
                    nds.bamBlob = nds.bwgBlob;
                    nds.bwgURI = undefined;
                    nds.bwgBlob = undefined;
                            
                    return addDasCompletionPage(nds, false, false, true);
                } else {
                    return binFormatErrorPage('You have selected a valid BAM file, but a corresponding index (.bai) file was not found.  Please index your BAM (samtools index) and place the BAI file in the same directory');
                }
        });
    }

    function binFormatErrorPage(message) {
        removeChildren(stabHolder);
        message = message || 'Custom data format not recognized';
        stabHolder.appendChild(makeElement('h2', 'Error adding custom data'));
        stabHolder.appendChild(makeElement('p', message));
        stabHolder.appendChild(makeElement('p', 'Currently supported formats are bigBed, bigWig, and BAM.'));
        customMode = 'reset-bin';
        return;
    }
                     
    var addDasCompletionPage = function(nds, coordsDetermined, quantDetermined, quantIrrelevant) {
        removeChildren(stabHolder);
        stabHolder.appendChild(makeElement('h2', 'Add custom data: step 2'));
        stabHolder.appendChild(document.createTextNode('Label: '));
        custName = makeElement('input', '', {value: nds.name});
        stabHolder.appendChild(custName);


        stabHolder.appendChild(document.createTextNode('User: '));
        custUser = makeElement('input', '');
        stabHolder.appendChild(custUser);
        stabHolder.appendChild(document.createTextNode('Pass: '));
        custPass = makeElement('input', '');
        stabHolder.appendChild(custPass);
        

        stabHolder.appendChild(makeElement('br'));
        stabHolder.appendChild(makeElement('br'));
        stabHolder.appendChild(makeElement('h4', 'Coordinate system: '));
        custCS = makeElement('select', null);
        custCS.appendChild(makeElement('option', thisB.coordSystem.auth + thisB.coordSystem.version, {value: '__default__'}));
        if (thisB.chains) {
            for (var csk in thisB.chains) {
                var cs = thisB.chains[csk].coords;
                custCS.appendChild(makeElement('option', cs.auth + cs.version, {value: csk}));
            }
        }
        custCS.value = nds.mapping || '__default__';
        stabHolder.appendChild(custCS);

        if (coordsDetermined) {
            stabHolder.appendChild(makeElement('p', "(Based on server response, probably doesn't need changing.)"));
        } else {
            stabHolder.appendChild(makeElement('p', [makeElement('b', 'Warning: '), "unable to determine the correct value from server responses.  Please check carefully."]));
            stabHolder.appendChild(makeElement('p', "If you don't see the mapping you're looking for, please contact thomas@biodalliance.org"));
        }

        if (!quantIrrelevant) {
            stabHolder.appendChild(document.createTextNode('Quantitative: '));
            custQuant = makeElement('input', null, {type: 'checkbox', checked: true});
            if (typeof nds.maxbins !== 'undefined') {
                custQuant.checked = nds.maxbins;
            }
            stabHolder.appendChild(custQuant);
            if (quantDetermined) {
                stabHolder.appendChild(makeElement('p', "(Based on server response, probably doesn't need changing.)"));
            } else {
                stabHolder.appendChild(makeElement('p', [makeElement('b', "Warning: "), "unable to determine correct value.  If in doubt, leave checked."]));
            }
        }

        if (nds.bwgBlob) {
            stabHolder.appendChild(makeElement('p', [makeElement('b', 'Warning: '), 'data added from local file.  Due to the browser security model, the track will disappear if you reload Dalliance.']));
        }

        custName.focus();
        customMode = 'finalize';
        dataToFinalize = nds;
    }


    var canButton = document.createElement('span');
    canButton.style.backgroundColor = 'rgb(230,230,250)';
    canButton.style.borderStyle = 'solid';
    canButton.style.borderColor = 'blue';
    canButton.style.borderWidth = '3px';
    canButton.style.padding = '2px';
    canButton.style.margin = '10px';
    canButton.style.width = '150px';
    // canButton.style.float = 'left';
    canButton.appendChild(document.createTextNode('Cancel'))
    canButton.addEventListener('mousedown', function(ev) {
        ev.stopPropagation(); ev.preventDefault();
        thisB.removeAllPopups();
    }, false);

    var refreshButton = makeElement('span', 'Refresh');
    refreshButton.style.backgroundColor = 'rgb(230,230,250)';
    refreshButton.style.borderStyle = 'solid';
    refreshButton.style.borderColor = 'blue';
    refreshButton.style.borderWidth = '3px';
    refreshButton.style.padding = '2px';
    refreshButton.style.margin = '10px';
    refreshButton.style.width = '120px';
    refreshButton.addEventListener('mousedown', function(ev) {
        ev.stopPropagation(); ev.preventDefault();
        thisB.queryRegistry(__mapping);
    }, false);
    this.makeTooltip(refreshButton, 'Click to re-fetch data from the DAS registry');

    var buttonHolder = makeElement('div', [addButton, canButton, refreshButton]);
    buttonHolder.style.margin = '10px';
    asform.appendChild(buttonHolder);

    popup.appendChild(asform);
    makeStab(thisB.availableSources);

    return this.popit(ev, 'Add DAS data', popup, {width: 600});
}
/* -*- mode: javascript; c-basic-offset: 4; indent-tabs-mode: nil -*- */

// 
// Dalliance Genome Explorer
// (c) Thomas Down 2006-2010
//
// twoBit.js: packed-binary reference sequences
//

var TWOBIT_MAGIC = 0x1a412743;

function TwoBitFile() {
}

function makeTwoBit(fetchable, cnt) {
    var tb = new TwoBitFile();
    tb.data = fetchable;

    tb.data.slice(0, 1024).fetch(function(r) {
        if (!r) {
            return cnt(null, "Couldn't access data");
        }
        var ba = new Uint8Array(r);
        var magic = readInt(ba, 0);
        if (magic != TWOBIT_MAGIC) {
            return cnt(null, "Not a .2bit fie");
        }

        var version = readInt(ba, 4);
        if (version != 0) {
            return cnt(null, 'Unsupported version ' + version);
        }
        
        tb.seqCount = readInt(ba, 8);
        tb.seqDict = {};
        var p = 16;
        for (var i = 0; i < tb.seqCount; ++i) {
            var ns = ba[p++];
            var name = '';
            for (var j = 1; j <= ns; ++j) {
                name += String.fromCharCode(ba[p++]);
            }
            var offset = readInt(ba, p);
            p += 4;
            tb.seqDict[name] = new TwoBitSeq(tb, offset);
        }
        return cnt(tb);
    });
}

TwoBitFile.prototype.getSeq = function(chr) {
    var seq = this.seqDict[chr];
    if (!seq) {
        seq = this.seqDict['chr' + chr];
    }
    return seq;
}

TwoBitFile.prototype.fetch = function(chr, min, max, cnt) {
    var seq = this.getSeq(chr);
    if (!seq) {
        return cnt(null, "Couldn't find " + chr);
    } else {
        seq.fetch(min, max, cnt);
    }
}

function TwoBitSeq(tbf, offset) {
    this.tbf = tbf;
    this.offset = offset;
}

TwoBitSeq.prototype.init = function(cnt) {
    if (this.seqOffset) {
        return cnt();
    }

    var thisB = this;
    thisB.tbf.data.slice(thisB.offset, 8).fetch(function(r1) {
        if (!r1) {
            return cnt('Fetch failed');
        }
        var ba = new Uint8Array(r1);
        thisB.length = readInt(ba, 0);
        thisB.nBlockCnt = readInt(ba, 4);
        thisB.tbf.data.slice(thisB.offset + 8, thisB.nBlockCnt*8 + 4).fetch(function(r2) {
            if (!r2) {
                return cnt('Fetch failed');
            }
            var ba = new Uint8Array(r2);
            var nbs = null;
            for (var b = 0; b < thisB.nBlockCnt; ++b) {
                var nbMin = readInt(ba, b * 4);
                var nbLen = readInt(ba, (b + thisB.nBlockCnt) * 4);
                var nb = new Range(nbMin, nbMin + nbLen - 1);
                if (!nbs) {
                    nbs = nb;
                } else {
                    nbs = union(nbs, nb);
                }
            }
            thisB.nBlocks = nbs;
            thisB.mBlockCnt = readInt(ba, thisB.nBlockCnt*8);
            thisB.seqLength = ((thisB.length + 3)/4)|0;
            thisB.seqOffset = thisB.offset + 16 + ((thisB.nBlockCnt + thisB.mBlockCnt) * 8);
            return cnt();
        });
    });
}

var TWOBIT_TABLE = ['T', 'C', 'A', 'G'];

TwoBitSeq.prototype.fetch = function(min, max, cnt) {
    --min; --max;       // Switch to zero-based.
    var thisB = this;
    this.init(function(error) {
        if (error) {
            return cnt(null, error);
        }

        var fetchMin = min >> 2;
        var fetchMax = max + 3 >> 2;
        if (fetchMin < 0 || fetchMax > thisB.seqLength) {
            return cnt('Coordinates out of bounds: ' + min + ':' + max);
        }

        thisB.tbf.data.slice(thisB.seqOffset + fetchMin, fetchMax - fetchMin).fetch(function(r) {
            if (r == null) {
                return cnt('SeqFetch failed');
            }
            var seqData = new Uint8Array(r);

            var nSpans = [];
            if (thisB.nBlocks) {
                var intr = intersection(new Range(min, max), thisB.nBlocks);
                if (intr) {
                    nSpans = intr.ranges();
                }
            }
            
            var seqstr = '';
            var ptr = min;
            function fillSeq(fsm) {
                while (ptr <= fsm) {
                    var bb = (ptr >> 2) - fetchMin;
                    var ni = ptr & 0x3;
                    var bv = seqData[bb];
                    var n;
                    if (ni == 0) {
                        n = (bv >> 6) & 0x3;
                    } else if (ni == 1) {
                        n = (bv >> 4) & 0x3;
                    } else if (ni == 2) {
                        n = (bv >> 2) & 0x3;
                    } else {
                        n = (bv) & 0x3;
                    }
                    seqstr += TWOBIT_TABLE[n];
                    ++ptr;
                }
            }
            
            for (var b = 0; b < nSpans.length; ++b) {
                var nb = nSpans[b];
                if (ptr > nb.min()) {
                    throw 'N mismatch...';
                }
                if (ptr < nb.min()) {
                    fillSeq(nb.min() - 1);
                }
                while (ptr < nb.max()) {
                    seqstr += 'N';
                    ++ptr;
                }
            }
            if (ptr < max) {
                fillSeq(max);
            }

            return cnt(seqstr);
        });
    });
}

TwoBitSeq.prototype.length = function(cnt) {
    var thisB = this;
    this.init(function(error) {
        if (error) {
            return cnt(null, error);
        } else {
            return cnt(thisB.length);
        }
    });
}
/* -*- mode: javascript; c-basic-offset: 4; indent-tabs-mode: nil -*- */

// 
// Dalliance Genome Explorer
// (c) Thomas Down 2006-2010
//
// utils.js: odds, sods, and ends.
//

var NUM_REGEXP = new RegExp('[0-9]+');

function stringToNumbersArray(str) {
    var nums = new Array();
    var m;
    while (m = NUM_REGEXP.exec(str)) {
        nums.push(m[0]);
        str=str.substring(m.index + (m[0].length));
    }
    return nums;
}

var STRICT_NUM_REGEXP = new RegExp('^[0-9]+$');

function stringToInt(str) {
    str = str.replace(new RegExp(',', 'g'), '');
    if (!STRICT_NUM_REGEXP.test(str)) {
        return null;
    }
    return str|0;
}

function pushnew(a, v) {
    for (var i = 0; i < a.length; ++i) {
        if (a[i] == v) {
            return;
        }
    }
    a.push(v);
}

function pusho(obj, k, v) {
    if (obj[k]) {
        obj[k].push(v);
    } else {
        obj[k] = [v];
    }
}

function pushnewo(obj, k, v) {
    var a = obj[k];
    if (a) {
        for (var i = 0; i < a.length; ++i) {    // indexOf requires JS16 :-(.
            if (a[i] == v) {
                return;
            }
        }
        a.push(v);
    } else {
        obj[k] = [v];
    }
}


function pick(a, b, c, d)
{
    if (a) {
        return a;
    } else if (b) {
        return b;
    } else if (c) {
        return c;
    } else if (d) {
        return d;
    }
}

function pushnew(l, o)
{
    for (var i = 0; i < l.length; ++i) {
        if (l[i] == o) {
            return;
        }
    }
    l.push(o);
}

function maybeConcat(a, b) {
    var l = [];
    if (a) {
        for (var i = 0; i < a.length; ++i) {
            pushnew(l, a[i]);
        }
    }
    if (b) {
        for (var i = 0; i < b.length; ++i) {
            pushnew(l, b[i]);
        }
    }
    return l;
}

function arrayIndexOf(a, x) {
    if (!a) {
        return -1;
    }

    for (var i = 0; i < a.length; ++i) {
        if (a[i] === x) {
            return i;
        }
    }
    return -1;
}

function arrayRemove(a, x) {
    var i = arrayIndexOf(a, x);
    if (i >= 0) {
        a.splice(i, 1);
        return true;
    }
    return false;
}

//
// DOM utilities
//


function makeElement(tag, children, attribs, styles)
{
    var ele = document.createElement(tag);
    if (children) {
        if (! (children instanceof Array)) {
            children = [children];
        }
        for (var i = 0; i < children.length; ++i) {
            var c = children[i];
            if (typeof c == 'string') {
                c = document.createTextNode(c);
            }
            ele.appendChild(c);
        }
    }
    
    if (attribs) {
        for (var l in attribs) {
            ele[l] = attribs[l];
        }
    }
    if (styles) {
        for (var l in styles) {
            ele.style[l] = styles[l];
        }
    }
    return ele;
}

function makeElementNS(namespace, tag, children, attribs)
{
    var ele = document.createElementNS(namespace, tag);
    if (children) {
        if (! (children instanceof Array)) {
            children = [children];
        }
        for (var i = 0; i < children.length; ++i) {
            var c = children[i];
            if (typeof c == 'string') {
                c = document.createTextNode(c);
            }
            ele.appendChild(c);
        }
    }
    
    setAttrs(ele, attribs);
    return ele;
}

var attr_name_cache = {};

function setAttr(node, key, value)
{
    var attr = attr_name_cache[key];
    if (!attr) {
        var _attr = '';
        for (var c = 0; c < key.length; ++c) {
            var cc = key.substring(c, c+1);
            var lcc = cc.toLowerCase();
            if (lcc != cc) {
                _attr = _attr + '-' + lcc;
            } else {
                _attr = _attr + cc;
            }
        }
        attr_name_cache[key] = _attr;
        attr = _attr;
    }
    node.setAttribute(attr, value);
}

function setAttrs(node, attribs)
{
    if (attribs) {
        for (var l in attribs) {
            setAttr(node, l, attribs[l]);
        }
    }
}



function removeChildren(node)
{
    if (!node || !node.childNodes) {
        return;
    }

    while (node.childNodes.length > 0) {
        node.removeChild(node.firstChild);
    }
}



//
// WARNING: not for general use!
//

function miniJSONify(o) {
    if (typeof o === 'undefined') {
        return 'undefined';
    } else if (o == null) {
        return 'null';
    } else if (typeof o == 'string') {
        return "'" + o + "'";
    } else if (typeof o == 'number') {
        return "" + o;
    } else if (typeof o == 'boolean') {
        return "" + o;
    } else if (typeof o == 'object') {
        if (o instanceof Array) {
            var s = null;
            for (var i = 0; i < o.length; ++i) {
                s = (s == null ? '' : (s + ', ')) + miniJSONify(o[i]);
            }
            return '[' + (s?s:'') + ']';
        } else {
            var s = null;
            for (var k in o) {
                if (k != undefined && typeof(o[k]) != 'function') {
                    s = (s == null ? '' : (s + ', ')) + k + ': ' + miniJSONify(o[k]);
                }
            }
            return '{' + (s?s:'') + '}';
        }
    } else {
        return (typeof o);
    }
}

function shallowCopy(o) {
    n = {};
    for (k in o) {
        n[k] = o[k];
    }
    return n;
}

function Observed(x) {
    this.value = x;
    this.listeners = [];
}

Observed.prototype.addListener = function(f) {
    this.listeners.push(f);
}

Observed.prototype.addListenerAndFire = function(f) {
    this.listeners.push(f);
    f(this.value);
}

Observed.prototype.removeListener = function(f) {
    arrayRemove(this.listeners, f);
}

Observed.prototype.get = function() {
    return this.value;
}

Observed.prototype.set = function(x) {
    this.value = x;
    for (var i = 0; i < this.listeners.length; ++i) {
        this.listeners[i](x);
    }
}

function Awaited() {
    this.queue = [];
}

Awaited.prototype.provide = function(x) {
    if (this.res) {
        throw "Resource has already been provided.";
    }

    this.res = x;
    for (var i = 0; i < this.queue.length; ++i) {
        this.queue[i](x);
    }
}

Awaited.prototype.await = function(f) {
    if (this.res) {
        f(this.res);
        return this.res;
    } else {
        this.queue.push(f);
    }
}


//
// Missing APIs
// 

if (!('trim' in String.prototype)) {
    String.prototype.trim = function() {
        return this.replace(/^\s+/, '').replace(/\s+$/, '');
    };
}
/* -*- mode: javascript; c-basic-offset: 4; indent-tabs-mode: nil -*- */

// 
// Dalliance Genome Explorer
// (c) Thomas Down 2006-2010
//
// version.js
//

var VERSION = {
    CONFIG: 3,
    MAJOR:  0,
    MINOR:  7,
    MICRO:  0,
    PATCH:  'pre5',
    BRANCH: ''
}

VERSION.toString = function() {
    var vs = '' + this.MAJOR + '.' + this.MINOR + '.' + this.MICRO;
    if (this.PATCH) {
        vs = vs + this.PATCH;
    }
    if (this.BRANCH && this.BRANCH != '') {
        vs = vs + '-' + this.BRANCH;
    }
    return vs;
}
/*
    http://www.JSON.org/json2.js
    2011-01-18

    Public Domain.

    NO WARRANTY EXPRESSED OR IMPLIED. USE AT YOUR OWN RISK.

    See http://www.JSON.org/js.html


    This code should be minified before deployment.
    See http://javascript.crockford.com/jsmin.html

    USE YOUR OWN COPY. IT IS EXTREMELY UNWISE TO LOAD CODE FROM SERVERS YOU DO
    NOT CONTROL.


    This file creates a global JSON object containing two methods: stringify
    and parse.

        JSON.stringify(value, replacer, space)
            value       any JavaScript value, usually an object or array.

            replacer    an optional parameter that determines how object
                        values are stringified for objects. It can be a
                        function or an array of strings.

            space       an optional parameter that specifies the indentation
                        of nested structures. If it is omitted, the text will
                        be packed without extra whitespace. If it is a number,
                        it will specify the number of spaces to indent at each
                        level. If it is a string (such as '\t' or '&nbsp;'),
                        it contains the characters used to indent at each level.

            This method produces a JSON text from a JavaScript value.

            When an object value is found, if the object contains a toJSON
            method, its toJSON method will be called and the result will be
            stringified. A toJSON method does not serialize: it returns the
            value represented by the name/value pair that should be serialized,
            or undefined if nothing should be serialized. The toJSON method
            will be passed the key associated with the value, and this will be
            bound to the value

            For example, this would serialize Dates as ISO strings.

                Date.prototype.toJSON = function (key) {
                    function f(n) {
                        // Format integers to have at least two digits.
                        return n < 10 ? '0' + n : n;
                    }

                    return this.getUTCFullYear()   + '-' +
                         f(this.getUTCMonth() + 1) + '-' +
                         f(this.getUTCDate())      + 'T' +
                         f(this.getUTCHours())     + ':' +
                         f(this.getUTCMinutes())   + ':' +
                         f(this.getUTCSeconds())   + 'Z';
                };

            You can provide an optional replacer method. It will be passed the
            key and value of each member, with this bound to the containing
            object. The value that is returned from your method will be
            serialized. If your method returns undefined, then the member will
            be excluded from the serialization.

            If the replacer parameter is an array of strings, then it will be
            used to select the members to be serialized. It filters the results
            such that only members with keys listed in the replacer array are
            stringified.

            Values that do not have JSON representations, such as undefined or
            functions, will not be serialized. Such values in objects will be
            dropped; in arrays they will be replaced with null. You can use
            a replacer function to replace those with JSON values.
            JSON.stringify(undefined) returns undefined.

            The optional space parameter produces a stringification of the
            value that is filled with line breaks and indentation to make it
            easier to read.

            If the space parameter is a non-empty string, then that string will
            be used for indentation. If the space parameter is a number, then
            the indentation will be that many spaces.

            Example:

            text = JSON.stringify(['e', {pluribus: 'unum'}]);
            // text is '["e",{"pluribus":"unum"}]'


            text = JSON.stringify(['e', {pluribus: 'unum'}], null, '\t');
            // text is '[\n\t"e",\n\t{\n\t\t"pluribus": "unum"\n\t}\n]'

            text = JSON.stringify([new Date()], function (key, value) {
                return this[key] instanceof Date ?
                    'Date(' + this[key] + ')' : value;
            });
            // text is '["Date(---current time---)"]'


        JSON.parse(text, reviver)
            This method parses a JSON text to produce an object or array.
            It can throw a SyntaxError exception.

            The optional reviver parameter is a function that can filter and
            transform the results. It receives each of the keys and values,
            and its return value is used instead of the original value.
            If it returns what it received, then the structure is not modified.
            If it returns undefined then the member is deleted.

            Example:

            // Parse the text. Values that look like ISO date strings will
            // be converted to Date objects.

            myData = JSON.parse(text, function (key, value) {
                var a;
                if (typeof value === 'string') {
                    a =
/^(\d{4})-(\d{2})-(\d{2})T(\d{2}):(\d{2}):(\d{2}(?:\.\d*)?)Z$/.exec(value);
                    if (a) {
                        return new Date(Date.UTC(+a[1], +a[2] - 1, +a[3], +a[4],
                            +a[5], +a[6]));
                    }
                }
                return value;
            });

            myData = JSON.parse('["Date(09/09/2001)"]', function (key, value) {
                var d;
                if (typeof value === 'string' &&
                        value.slice(0, 5) === 'Date(' &&
                        value.slice(-1) === ')') {
                    d = new Date(value.slice(5, -1));
                    if (d) {
                        return d;
                    }
                }
                return value;
            });


    This is a reference implementation. You are free to copy, modify, or
    redistribute.
*/

/*jslint evil: true, strict: false, regexp: false */

/*members "", "\b", "\t", "\n", "\f", "\r", "\"", JSON, "\\", apply,
    call, charCodeAt, getUTCDate, getUTCFullYear, getUTCHours,
    getUTCMinutes, getUTCMonth, getUTCSeconds, hasOwnProperty, join,
    lastIndex, length, parse, prototype, push, replace, slice, stringify,
    test, toJSON, toString, valueOf
*/


// Create a JSON object only if one does not already exist. We create the
// methods in a closure to avoid creating global variables.

var JSON;
if (!JSON) {
    JSON = {};
}

(function () {
    "use strict";

    function f(n) {
        // Format integers to have at least two digits.
        return n < 10 ? '0' + n : n;
    }

    if (typeof Date.prototype.toJSON !== 'function') {

        Date.prototype.toJSON = function (key) {

            return isFinite(this.valueOf()) ?
                this.getUTCFullYear()     + '-' +
                f(this.getUTCMonth() + 1) + '-' +
                f(this.getUTCDate())      + 'T' +
                f(this.getUTCHours())     + ':' +
                f(this.getUTCMinutes())   + ':' +
                f(this.getUTCSeconds())   + 'Z' : null;
        };

        String.prototype.toJSON      =
            Number.prototype.toJSON  =
            Boolean.prototype.toJSON = function (key) {
                return this.valueOf();
            };
    }

    var cx = /[\u0000\u00ad\u0600-\u0604\u070f\u17b4\u17b5\u200c-\u200f\u2028-\u202f\u2060-\u206f\ufeff\ufff0-\uffff]/g,
        escapable = /[\\\"\x00-\x1f\x7f-\x9f\u00ad\u0600-\u0604\u070f\u17b4\u17b5\u200c-\u200f\u2028-\u202f\u2060-\u206f\ufeff\ufff0-\uffff]/g,
        gap,
        indent,
        meta = {    // table of character substitutions
            '\b': '\\b',
            '\t': '\\t',
            '\n': '\\n',
            '\f': '\\f',
            '\r': '\\r',
            '"' : '\\"',
            '\\': '\\\\'
        },
        rep;


    function quote(string) {

// If the string contains no control characters, no quote characters, and no
// backslash characters, then we can safely slap some quotes around it.
// Otherwise we must also replace the offending characters with safe escape
// sequences.

        escapable.lastIndex = 0;
        return escapable.test(string) ? '"' + string.replace(escapable, function (a) {
            var c = meta[a];
            return typeof c === 'string' ? c :
                '\\u' + ('0000' + a.charCodeAt(0).toString(16)).slice(-4);
        }) + '"' : '"' + string + '"';
    }


    function str(key, holder) {

// Produce a string from holder[key].

        var i,          // The loop counter.
            k,          // The member key.
            v,          // The member value.
            length,
            mind = gap,
            partial,
            value = holder[key];

// If the value has a toJSON method, call it to obtain a replacement value.

        if (value && typeof value === 'object' &&
                typeof value.toJSON === 'function') {
            value = value.toJSON(key);
        }

// If we were called with a replacer function, then call the replacer to
// obtain a replacement value.

        if (typeof rep === 'function') {
            value = rep.call(holder, key, value);
        }

// What happens next depends on the value's type.

        switch (typeof value) {
        case 'string':
            return quote(value);

        case 'number':

// JSON numbers must be finite. Encode non-finite numbers as null.

            return isFinite(value) ? String(value) : 'null';

        case 'boolean':
        case 'null':

// If the value is a boolean or null, convert it to a string. Note:
// typeof null does not produce 'null'. The case is included here in
// the remote chance that this gets fixed someday.

            return String(value);

// If the type is 'object', we might be dealing with an object or an array or
// null.

        case 'object':

// Due to a specification blunder in ECMAScript, typeof null is 'object',
// so watch out for that case.

            if (!value) {
                return 'null';
            }

// Make an array to hold the partial results of stringifying this object value.

            gap += indent;
            partial = [];

// Is the value an array?

            if (Object.prototype.toString.apply(value) === '[object Array]') {

// The value is an array. Stringify every element. Use null as a placeholder
// for non-JSON values.

                length = value.length;
                for (i = 0; i < length; i += 1) {
                    partial[i] = str(i, value) || 'null';
                }

// Join all of the elements together, separated with commas, and wrap them in
// brackets.

                v = partial.length === 0 ? '[]' : gap ?
                    '[\n' + gap + partial.join(',\n' + gap) + '\n' + mind + ']' :
                    '[' + partial.join(',') + ']';
                gap = mind;
                return v;
            }

// If the replacer is an array, use it to select the members to be stringified.

            if (rep && typeof rep === 'object') {
                length = rep.length;
                for (i = 0; i < length; i += 1) {
                    k = rep[i];
                    if (typeof k === 'string') {
                        v = str(k, value);
                        if (v) {
                            partial.push(quote(k) + (gap ? ': ' : ':') + v);
                        }
                    }
                }
            } else {

// Otherwise, iterate through all of the keys in the object.

                for (k in value) {
                    if (Object.hasOwnProperty.call(value, k)) {
                        v = str(k, value);
                        if (v) {
                            partial.push(quote(k) + (gap ? ': ' : ':') + v);
                        }
                    }
                }
            }

// Join all of the member texts together, separated with commas,
// and wrap them in braces.

            v = partial.length === 0 ? '{}' : gap ?
                '{\n' + gap + partial.join(',\n' + gap) + '\n' + mind + '}' :
                '{' + partial.join(',') + '}';
            gap = mind;
            return v;
        }
    }

// If the JSON object does not yet have a stringify method, give it one.

    if (typeof JSON.stringify !== 'function') {
        JSON.stringify = function (value, replacer, space) {

// The stringify method takes a value and an optional replacer, and an optional
// space parameter, and returns a JSON text. The replacer can be a function
// that can replace values, or an array of strings that will select the keys.
// A default replacer method can be provided. Use of the space parameter can
// produce text that is more easily readable.

            var i;
            gap = '';
            indent = '';

// If the space parameter is a number, make an indent string containing that
// many spaces.

            if (typeof space === 'number') {
                for (i = 0; i < space; i += 1) {
                    indent += ' ';
                }

// If the space parameter is a string, it will be used as the indent string.

            } else if (typeof space === 'string') {
                indent = space;
            }

// If there is a replacer, it must be a function or an array.
// Otherwise, throw an error.

            rep = replacer;
            if (replacer && typeof replacer !== 'function' &&
                    (typeof replacer !== 'object' ||
                    typeof replacer.length !== 'number')) {
                throw new Error('JSON.stringify');
            }

// Make a fake root object containing our value under the key of ''.
// Return the result of stringifying the value.

            return str('', {'': value});
        };
    }


// If the JSON object does not yet have a parse method, give it one.

    if (typeof JSON.parse !== 'function') {
        JSON.parse = function (text, reviver) {

// The parse method takes a text and an optional reviver function, and returns
// a JavaScript value if the text is a valid JSON text.

            var j;

            function walk(holder, key) {

// The walk method is used to recursively walk the resulting structure so
// that modifications can be made.

                var k, v, value = holder[key];
                if (value && typeof value === 'object') {
                    for (k in value) {
                        if (Object.hasOwnProperty.call(value, k)) {
                            v = walk(value, k);
                            if (v !== undefined) {
                                value[k] = v;
                            } else {
                                delete value[k];
                            }
                        }
                    }
                }
                return reviver.call(holder, key, value);
            }


// Parsing happens in four stages. In the first stage, we replace certain
// Unicode characters with escape sequences. JavaScript handles many characters
// incorrectly, either silently deleting them, or treating them as line endings.

            text = String(text);
            cx.lastIndex = 0;
            if (cx.test(text)) {
                text = text.replace(cx, function (a) {
                    return '\\u' +
                        ('0000' + a.charCodeAt(0).toString(16)).slice(-4);
                });
            }

// In the second stage, we run the text against regular expressions that look
// for non-JSON patterns. We are especially concerned with '()' and 'new'
// because they can cause invocation, and '=' because it can cause mutation.
// But just to be safe, we want to reject all unexpected forms.

// We split the second stage into 4 regexp operations in order to work around
// crippling inefficiencies in IE's and Safari's regexp engines. First we
// replace the JSON backslash pairs with '@' (a non-JSON character). Second, we
// replace all simple value tokens with ']' characters. Third, we delete all
// open brackets that follow a colon or comma or that begin the text. Finally,
// we look to see that the remaining characters are only whitespace or ']' or
// ',' or ':' or '{' or '}'. If that is so, then the text is safe for eval.

            if (/^[\],:{}\s]*$/
                    .test(text.replace(/\\(?:["\\\/bfnrt]|u[0-9a-fA-F]{4})/g, '@')
                        .replace(/"[^"\\\n\r]*"|true|false|null|-?\d+(?:\.\d*)?(?:[eE][+\-]?\d+)?/g, ']')
                        .replace(/(?:^|:|,)(?:\s*\[)+/g, ''))) {

// In the third stage we use the eval function to compile the text into a
// JavaScript structure. The '{' operator is subject to a syntactic ambiguity
// in JavaScript: it can begin a block or an object literal. We wrap the text
// in parens to eliminate the ambiguity.

                j = eval('(' + text + ')');

// In the optional fourth stage, we recursively walk the new structure, passing
// each name/value pair to a reviver function for possible transformation.

                return typeof reviver === 'function' ?
                    walk({'': j}, '') : j;
            }

// If the text is not JSON parseable, then a SyntaxError is thrown.

            throw new SyntaxError('JSON.parse');
        };
    }
}());
/* -*- mode: javascript; c-basic-offset: 4; indent-tabs-mode: nil -*- */

// 
// Javascript ZLib
// By Thomas Down 2010-2011
//
// Based very heavily on portions of jzlib (by ymnk@jcraft.com), who in
// turn credits Jean-loup Gailly and Mark Adler for the original zlib code.
//
// inflate.js: ZLib inflate code
//

//
// Shared constants
//

var MAX_WBITS=15; // 32K LZ77 window
var DEF_WBITS=MAX_WBITS;
var MAX_MEM_LEVEL=9;
var MANY=1440;
var BMAX = 15;

// preset dictionary flag in zlib header
var PRESET_DICT=0x20;

var Z_NO_FLUSH=0;
var Z_PARTIAL_FLUSH=1;
var Z_SYNC_FLUSH=2;
var Z_FULL_FLUSH=3;
var Z_FINISH=4;

var Z_DEFLATED=8;

var Z_OK=0;
var Z_STREAM_END=1;
var Z_NEED_DICT=2;
var Z_ERRNO=-1;
var Z_STREAM_ERROR=-2;
var Z_DATA_ERROR=-3;
var Z_MEM_ERROR=-4;
var Z_BUF_ERROR=-5;
var Z_VERSION_ERROR=-6;

var METHOD=0;   // waiting for method byte
var FLAG=1;     // waiting for flag byte
var DICT4=2;    // four dictionary check bytes to go
var DICT3=3;    // three dictionary check bytes to go
var DICT2=4;    // two dictionary check bytes to go
var DICT1=5;    // one dictionary check byte to go
var DICT0=6;    // waiting for inflateSetDictionary
var BLOCKS=7;   // decompressing blocks
var CHECK4=8;   // four check bytes to go
var CHECK3=9;   // three check bytes to go
var CHECK2=10;  // two check bytes to go
var CHECK1=11;  // one check byte to go
var DONE=12;    // finished check, done
var BAD=13;     // got an error--stay here

var inflate_mask = [0x00000000, 0x00000001, 0x00000003, 0x00000007, 0x0000000f, 0x0000001f, 0x0000003f, 0x0000007f, 0x000000ff, 0x000001ff, 0x000003ff, 0x000007ff, 0x00000fff, 0x00001fff, 0x00003fff, 0x00007fff, 0x0000ffff];

var IB_TYPE=0;  // get type bits (3, including end bit)
var IB_LENS=1;  // get lengths for stored
var IB_STORED=2;// processing stored block
var IB_TABLE=3; // get table lengths
var IB_BTREE=4; // get bit lengths tree for a dynamic block
var IB_DTREE=5; // get length, distance trees for a dynamic block
var IB_CODES=6; // processing fixed or dynamic block
var IB_DRY=7;   // output remaining window bytes
var IB_DONE=8;  // finished last block, done
var IB_BAD=9;   // ot a data error--stuck here

var fixed_bl = 9;
var fixed_bd = 5;

var fixed_tl = [
    96,7,256, 0,8,80, 0,8,16, 84,8,115,
    82,7,31, 0,8,112, 0,8,48, 0,9,192,
    80,7,10, 0,8,96, 0,8,32, 0,9,160,
    0,8,0, 0,8,128, 0,8,64, 0,9,224,
    80,7,6, 0,8,88, 0,8,24, 0,9,144,
    83,7,59, 0,8,120, 0,8,56, 0,9,208,
    81,7,17, 0,8,104, 0,8,40, 0,9,176,
    0,8,8, 0,8,136, 0,8,72, 0,9,240,
    80,7,4, 0,8,84, 0,8,20, 85,8,227,
    83,7,43, 0,8,116, 0,8,52, 0,9,200,
    81,7,13, 0,8,100, 0,8,36, 0,9,168,
    0,8,4, 0,8,132, 0,8,68, 0,9,232,
    80,7,8, 0,8,92, 0,8,28, 0,9,152,
    84,7,83, 0,8,124, 0,8,60, 0,9,216,
    82,7,23, 0,8,108, 0,8,44, 0,9,184,
    0,8,12, 0,8,140, 0,8,76, 0,9,248,
    80,7,3, 0,8,82, 0,8,18, 85,8,163,
    83,7,35, 0,8,114, 0,8,50, 0,9,196,
    81,7,11, 0,8,98, 0,8,34, 0,9,164,
    0,8,2, 0,8,130, 0,8,66, 0,9,228,
    80,7,7, 0,8,90, 0,8,26, 0,9,148,
    84,7,67, 0,8,122, 0,8,58, 0,9,212,
    82,7,19, 0,8,106, 0,8,42, 0,9,180,
    0,8,10, 0,8,138, 0,8,74, 0,9,244,
    80,7,5, 0,8,86, 0,8,22, 192,8,0,
    83,7,51, 0,8,118, 0,8,54, 0,9,204,
    81,7,15, 0,8,102, 0,8,38, 0,9,172,
    0,8,6, 0,8,134, 0,8,70, 0,9,236,
    80,7,9, 0,8,94, 0,8,30, 0,9,156,
    84,7,99, 0,8,126, 0,8,62, 0,9,220,
    82,7,27, 0,8,110, 0,8,46, 0,9,188,
    0,8,14, 0,8,142, 0,8,78, 0,9,252,
    96,7,256, 0,8,81, 0,8,17, 85,8,131,
    82,7,31, 0,8,113, 0,8,49, 0,9,194,
    80,7,10, 0,8,97, 0,8,33, 0,9,162,
    0,8,1, 0,8,129, 0,8,65, 0,9,226,
    80,7,6, 0,8,89, 0,8,25, 0,9,146,
    83,7,59, 0,8,121, 0,8,57, 0,9,210,
    81,7,17, 0,8,105, 0,8,41, 0,9,178,
    0,8,9, 0,8,137, 0,8,73, 0,9,242,
    80,7,4, 0,8,85, 0,8,21, 80,8,258,
    83,7,43, 0,8,117, 0,8,53, 0,9,202,
    81,7,13, 0,8,101, 0,8,37, 0,9,170,
    0,8,5, 0,8,133, 0,8,69, 0,9,234,
    80,7,8, 0,8,93, 0,8,29, 0,9,154,
    84,7,83, 0,8,125, 0,8,61, 0,9,218,
    82,7,23, 0,8,109, 0,8,45, 0,9,186,
    0,8,13, 0,8,141, 0,8,77, 0,9,250,
    80,7,3, 0,8,83, 0,8,19, 85,8,195,
    83,7,35, 0,8,115, 0,8,51, 0,9,198,
    81,7,11, 0,8,99, 0,8,35, 0,9,166,
    0,8,3, 0,8,131, 0,8,67, 0,9,230,
    80,7,7, 0,8,91, 0,8,27, 0,9,150,
    84,7,67, 0,8,123, 0,8,59, 0,9,214,
    82,7,19, 0,8,107, 0,8,43, 0,9,182,
    0,8,11, 0,8,139, 0,8,75, 0,9,246,
    80,7,5, 0,8,87, 0,8,23, 192,8,0,
    83,7,51, 0,8,119, 0,8,55, 0,9,206,
    81,7,15, 0,8,103, 0,8,39, 0,9,174,
    0,8,7, 0,8,135, 0,8,71, 0,9,238,
    80,7,9, 0,8,95, 0,8,31, 0,9,158,
    84,7,99, 0,8,127, 0,8,63, 0,9,222,
    82,7,27, 0,8,111, 0,8,47, 0,9,190,
    0,8,15, 0,8,143, 0,8,79, 0,9,254,
    96,7,256, 0,8,80, 0,8,16, 84,8,115,
    82,7,31, 0,8,112, 0,8,48, 0,9,193,

    80,7,10, 0,8,96, 0,8,32, 0,9,161,
    0,8,0, 0,8,128, 0,8,64, 0,9,225,
    80,7,6, 0,8,88, 0,8,24, 0,9,145,
    83,7,59, 0,8,120, 0,8,56, 0,9,209,
    81,7,17, 0,8,104, 0,8,40, 0,9,177,
    0,8,8, 0,8,136, 0,8,72, 0,9,241,
    80,7,4, 0,8,84, 0,8,20, 85,8,227,
    83,7,43, 0,8,116, 0,8,52, 0,9,201,
    81,7,13, 0,8,100, 0,8,36, 0,9,169,
    0,8,4, 0,8,132, 0,8,68, 0,9,233,
    80,7,8, 0,8,92, 0,8,28, 0,9,153,
    84,7,83, 0,8,124, 0,8,60, 0,9,217,
    82,7,23, 0,8,108, 0,8,44, 0,9,185,
    0,8,12, 0,8,140, 0,8,76, 0,9,249,
    80,7,3, 0,8,82, 0,8,18, 85,8,163,
    83,7,35, 0,8,114, 0,8,50, 0,9,197,
    81,7,11, 0,8,98, 0,8,34, 0,9,165,
    0,8,2, 0,8,130, 0,8,66, 0,9,229,
    80,7,7, 0,8,90, 0,8,26, 0,9,149,
    84,7,67, 0,8,122, 0,8,58, 0,9,213,
    82,7,19, 0,8,106, 0,8,42, 0,9,181,
    0,8,10, 0,8,138, 0,8,74, 0,9,245,
    80,7,5, 0,8,86, 0,8,22, 192,8,0,
    83,7,51, 0,8,118, 0,8,54, 0,9,205,
    81,7,15, 0,8,102, 0,8,38, 0,9,173,
    0,8,6, 0,8,134, 0,8,70, 0,9,237,
    80,7,9, 0,8,94, 0,8,30, 0,9,157,
    84,7,99, 0,8,126, 0,8,62, 0,9,221,
    82,7,27, 0,8,110, 0,8,46, 0,9,189,
    0,8,14, 0,8,142, 0,8,78, 0,9,253,
    96,7,256, 0,8,81, 0,8,17, 85,8,131,
    82,7,31, 0,8,113, 0,8,49, 0,9,195,
    80,7,10, 0,8,97, 0,8,33, 0,9,163,
    0,8,1, 0,8,129, 0,8,65, 0,9,227,
    80,7,6, 0,8,89, 0,8,25, 0,9,147,
    83,7,59, 0,8,121, 0,8,57, 0,9,211,
    81,7,17, 0,8,105, 0,8,41, 0,9,179,
    0,8,9, 0,8,137, 0,8,73, 0,9,243,
    80,7,4, 0,8,85, 0,8,21, 80,8,258,
    83,7,43, 0,8,117, 0,8,53, 0,9,203,
    81,7,13, 0,8,101, 0,8,37, 0,9,171,
    0,8,5, 0,8,133, 0,8,69, 0,9,235,
    80,7,8, 0,8,93, 0,8,29, 0,9,155,
    84,7,83, 0,8,125, 0,8,61, 0,9,219,
    82,7,23, 0,8,109, 0,8,45, 0,9,187,
    0,8,13, 0,8,141, 0,8,77, 0,9,251,
    80,7,3, 0,8,83, 0,8,19, 85,8,195,
    83,7,35, 0,8,115, 0,8,51, 0,9,199,
    81,7,11, 0,8,99, 0,8,35, 0,9,167,
    0,8,3, 0,8,131, 0,8,67, 0,9,231,
    80,7,7, 0,8,91, 0,8,27, 0,9,151,
    84,7,67, 0,8,123, 0,8,59, 0,9,215,
    82,7,19, 0,8,107, 0,8,43, 0,9,183,
    0,8,11, 0,8,139, 0,8,75, 0,9,247,
    80,7,5, 0,8,87, 0,8,23, 192,8,0,
    83,7,51, 0,8,119, 0,8,55, 0,9,207,
    81,7,15, 0,8,103, 0,8,39, 0,9,175,
    0,8,7, 0,8,135, 0,8,71, 0,9,239,
    80,7,9, 0,8,95, 0,8,31, 0,9,159,
    84,7,99, 0,8,127, 0,8,63, 0,9,223,
    82,7,27, 0,8,111, 0,8,47, 0,9,191,
    0,8,15, 0,8,143, 0,8,79, 0,9,255
];
var fixed_td = [
    80,5,1, 87,5,257, 83,5,17, 91,5,4097,
    81,5,5, 89,5,1025, 85,5,65, 93,5,16385,
    80,5,3, 88,5,513, 84,5,33, 92,5,8193,
    82,5,9, 90,5,2049, 86,5,129, 192,5,24577,
    80,5,2, 87,5,385, 83,5,25, 91,5,6145,
    81,5,7, 89,5,1537, 85,5,97, 93,5,24577,
    80,5,4, 88,5,769, 84,5,49, 92,5,12289,
    82,5,13, 90,5,3073, 86,5,193, 192,5,24577
];

  // Tables for deflate from PKZIP's appnote.txt.
  var cplens = [ // Copy lengths for literal codes 257..285
        3, 4, 5, 6, 7, 8, 9, 10, 11, 13, 15, 17, 19, 23, 27, 31,
        35, 43, 51, 59, 67, 83, 99, 115, 131, 163, 195, 227, 258, 0, 0
  ];

  // see note #13 above about 258
  var cplext = [ // Extra bits for literal codes 257..285
        0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 2, 2, 2, 2,
        3, 3, 3, 3, 4, 4, 4, 4, 5, 5, 5, 5, 0, 112, 112  // 112==invalid
  ];

 var cpdist = [ // Copy offsets for distance codes 0..29
        1, 2, 3, 4, 5, 7, 9, 13, 17, 25, 33, 49, 65, 97, 129, 193,
        257, 385, 513, 769, 1025, 1537, 2049, 3073, 4097, 6145,
        8193, 12289, 16385, 24577
  ];

  var cpdext = [ // Extra bits for distance codes
        0, 0, 0, 0, 1, 1, 2, 2, 3, 3, 4, 4, 5, 5, 6, 6,
        7, 7, 8, 8, 9, 9, 10, 10, 11, 11,
        12, 12, 13, 13];

//
// ZStream.java
//

function ZStream() {
}


ZStream.prototype.inflateInit = function(w, nowrap) {
    if (!w) {
	w = DEF_WBITS;
    }
    if (nowrap) {
	nowrap = false;
    }
    this.istate = new Inflate();
    return this.istate.inflateInit(this, nowrap?-w:w);
}

ZStream.prototype.inflate = function(f) {
    if(this.istate==null) return Z_STREAM_ERROR;
    return this.istate.inflate(this, f);
}

ZStream.prototype.inflateEnd = function(){
    if(this.istate==null) return Z_STREAM_ERROR;
    var ret=istate.inflateEnd(this);
    this.istate = null;
    return ret;
}
ZStream.prototype.inflateSync = function(){
    // if(istate == null) return Z_STREAM_ERROR;
    return istate.inflateSync(this);
}
ZStream.prototype.inflateSetDictionary = function(dictionary, dictLength){
    // if(istate == null) return Z_STREAM_ERROR;
    return istate.inflateSetDictionary(this, dictionary, dictLength);
}

/*

  public int deflateInit(int level){
    return deflateInit(level, MAX_WBITS);
  }
  public int deflateInit(int level, boolean nowrap){
    return deflateInit(level, MAX_WBITS, nowrap);
  }
  public int deflateInit(int level, int bits){
    return deflateInit(level, bits, false);
  }
  public int deflateInit(int level, int bits, boolean nowrap){
    dstate=new Deflate();
    return dstate.deflateInit(this, level, nowrap?-bits:bits);
  }
  public int deflate(int flush){
    if(dstate==null){
      return Z_STREAM_ERROR;
    }
    return dstate.deflate(this, flush);
  }
  public int deflateEnd(){
    if(dstate==null) return Z_STREAM_ERROR;
    int ret=dstate.deflateEnd();
    dstate=null;
    return ret;
  }
  public int deflateParams(int level, int strategy){
    if(dstate==null) return Z_STREAM_ERROR;
    return dstate.deflateParams(this, level, strategy);
  }
  public int deflateSetDictionary (byte[] dictionary, int dictLength){
    if(dstate == null)
      return Z_STREAM_ERROR;
    return dstate.deflateSetDictionary(this, dictionary, dictLength);
  }

*/

/*
  // Flush as much pending output as possible. All deflate() output goes
  // through this function so some applications may wish to modify it
  // to avoid allocating a large strm->next_out buffer and copying into it.
  // (See also read_buf()).
  void flush_pending(){
    int len=dstate.pending;

    if(len>avail_out) len=avail_out;
    if(len==0) return;

    if(dstate.pending_buf.length<=dstate.pending_out ||
       next_out.length<=next_out_index ||
       dstate.pending_buf.length<(dstate.pending_out+len) ||
       next_out.length<(next_out_index+len)){
      System.out.println(dstate.pending_buf.length+", "+dstate.pending_out+
			 ", "+next_out.length+", "+next_out_index+", "+len);
      System.out.println("avail_out="+avail_out);
    }

    System.arraycopy(dstate.pending_buf, dstate.pending_out,
		     next_out, next_out_index, len);

    next_out_index+=len;
    dstate.pending_out+=len;
    total_out+=len;
    avail_out-=len;
    dstate.pending-=len;
    if(dstate.pending==0){
      dstate.pending_out=0;
    }
  }

  // Read a new buffer from the current input stream, update the adler32
  // and total number of bytes read.  All deflate() input goes through
  // this function so some applications may wish to modify it to avoid
  // allocating a large strm->next_in buffer and copying from it.
  // (See also flush_pending()).
  int read_buf(byte[] buf, int start, int size) {
    int len=avail_in;

    if(len>size) len=size;
    if(len==0) return 0;

    avail_in-=len;

    if(dstate.noheader==0) {
      adler=_adler.adler32(adler, next_in, next_in_index, len);
    }
    System.arraycopy(next_in, next_in_index, buf, start, len);
    next_in_index  += len;
    total_in += len;
    return len;
  }

  public void free(){
    next_in=null;
    next_out=null;
    msg=null;
    _adler=null;
  }
}
*/


//
// Inflate.java
//

function Inflate() {
    this.was = [0];
}

Inflate.prototype.inflateReset = function(z) {
    if(z == null || z.istate == null) return Z_STREAM_ERROR;
    
    z.total_in = z.total_out = 0;
    z.msg = null;
    z.istate.mode = z.istate.nowrap!=0 ? BLOCKS : METHOD;
    z.istate.blocks.reset(z, null);
    return Z_OK;
}

Inflate.prototype.inflateEnd = function(z){
    if(this.blocks != null)
      this.blocks.free(z);
    this.blocks=null;
    return Z_OK;
}

Inflate.prototype.inflateInit = function(z, w){
    z.msg = null;
    this.blocks = null;

    // handle undocumented nowrap option (no zlib header or check)
    nowrap = 0;
    if(w < 0){
      w = - w;
      nowrap = 1;
    }

    // set window size
    if(w<8 ||w>15){
      this.inflateEnd(z);
      return Z_STREAM_ERROR;
    }
    this.wbits=w;

    z.istate.blocks=new InfBlocks(z, 
				  z.istate.nowrap!=0 ? null : this,
				  1<<w);

    // reset state
    this.inflateReset(z);
    return Z_OK;
  }

Inflate.prototype.inflate = function(z, f){
    var r, b;

    if(z == null || z.istate == null || z.next_in == null)
      return Z_STREAM_ERROR;
    f = f == Z_FINISH ? Z_BUF_ERROR : Z_OK;
    r = Z_BUF_ERROR;
    while (true){
      switch (z.istate.mode){
      case METHOD:

        if(z.avail_in==0)return r;r=f;

        z.avail_in--; z.total_in++;
        if(((z.istate.method = z.next_in[z.next_in_index++])&0xf)!=Z_DEFLATED){
          z.istate.mode = BAD;
          z.msg="unknown compression method";
          z.istate.marker = 5;       // can't try inflateSync
          break;
        }
        if((z.istate.method>>4)+8>z.istate.wbits){
          z.istate.mode = BAD;
          z.msg="invalid window size";
          z.istate.marker = 5;       // can't try inflateSync
          break;
        }
        z.istate.mode=FLAG;
      case FLAG:

        if(z.avail_in==0)return r;r=f;

        z.avail_in--; z.total_in++;
        b = (z.next_in[z.next_in_index++])&0xff;

        if((((z.istate.method << 8)+b) % 31)!=0){
          z.istate.mode = BAD;
          z.msg = "incorrect header check";
          z.istate.marker = 5;       // can't try inflateSync
          break;
        }

        if((b&PRESET_DICT)==0){
          z.istate.mode = BLOCKS;
          break;
        }
        z.istate.mode = DICT4;
      case DICT4:

        if(z.avail_in==0)return r;r=f;

        z.avail_in--; z.total_in++;
        z.istate.need=((z.next_in[z.next_in_index++]&0xff)<<24)&0xff000000;
        z.istate.mode=DICT3;
      case DICT3:

        if(z.avail_in==0)return r;r=f;

        z.avail_in--; z.total_in++;
        z.istate.need+=((z.next_in[z.next_in_index++]&0xff)<<16)&0xff0000;
        z.istate.mode=DICT2;
      case DICT2:

        if(z.avail_in==0)return r;r=f;

        z.avail_in--; z.total_in++;
        z.istate.need+=((z.next_in[z.next_in_index++]&0xff)<<8)&0xff00;
        z.istate.mode=DICT1;
      case DICT1:

        if(z.avail_in==0)return r;r=f;

        z.avail_in--; z.total_in++;
        z.istate.need += (z.next_in[z.next_in_index++]&0xff);
        z.adler = z.istate.need;
        z.istate.mode = DICT0;
        return Z_NEED_DICT;
      case DICT0:
        z.istate.mode = BAD;
        z.msg = "need dictionary";
        z.istate.marker = 0;       // can try inflateSync
        return Z_STREAM_ERROR;
      case BLOCKS:

        r = z.istate.blocks.proc(z, r);
        if(r == Z_DATA_ERROR){
          z.istate.mode = BAD;
          z.istate.marker = 0;     // can try inflateSync
          break;
        }
        if(r == Z_OK){
          r = f;
        }
        if(r != Z_STREAM_END){
          return r;
        }
        r = f;
        z.istate.blocks.reset(z, z.istate.was);
        if(z.istate.nowrap!=0){
          z.istate.mode=DONE;
          break;
        }
        z.istate.mode=CHECK4;
      case CHECK4:

        if(z.avail_in==0)return r;r=f;

        z.avail_in--; z.total_in++;
        z.istate.need=((z.next_in[z.next_in_index++]&0xff)<<24)&0xff000000;
        z.istate.mode=CHECK3;
      case CHECK3:

        if(z.avail_in==0)return r;r=f;

        z.avail_in--; z.total_in++;
        z.istate.need+=((z.next_in[z.next_in_index++]&0xff)<<16)&0xff0000;
        z.istate.mode = CHECK2;
      case CHECK2:

        if(z.avail_in==0)return r;r=f;

        z.avail_in--; z.total_in++;
        z.istate.need+=((z.next_in[z.next_in_index++]&0xff)<<8)&0xff00;
        z.istate.mode = CHECK1;
      case CHECK1:

        if(z.avail_in==0)return r;r=f;

        z.avail_in--; z.total_in++;
        z.istate.need+=(z.next_in[z.next_in_index++]&0xff);

        if(((z.istate.was[0])) != ((z.istate.need))){
          z.istate.mode = BAD;
          z.msg = "incorrect data check";
          z.istate.marker = 5;       // can't try inflateSync
          break;
        }

        z.istate.mode = DONE;
      case DONE:
        return Z_STREAM_END;
      case BAD:
        return Z_DATA_ERROR;
      default:
        return Z_STREAM_ERROR;
      }
    }
  }


Inflate.prototype.inflateSetDictionary = function(z,  dictionary, dictLength) {
    var index=0;
    var length = dictLength;
    if(z==null || z.istate == null|| z.istate.mode != DICT0)
      return Z_STREAM_ERROR;

    if(z._adler.adler32(1, dictionary, 0, dictLength)!=z.adler){
      return Z_DATA_ERROR;
    }

    z.adler = z._adler.adler32(0, null, 0, 0);

    if(length >= (1<<z.istate.wbits)){
      length = (1<<z.istate.wbits)-1;
      index=dictLength - length;
    }
    z.istate.blocks.set_dictionary(dictionary, index, length);
    z.istate.mode = BLOCKS;
    return Z_OK;
  }

//  static private byte[] mark = {(byte)0, (byte)0, (byte)0xff, (byte)0xff};
var mark = [0, 0, 255, 255]

Inflate.prototype.inflateSync = function(z){
    var n;       // number of bytes to look at
    var p;       // pointer to bytes
    var m;       // number of marker bytes found in a row
    var r, w;   // temporaries to save total_in and total_out

    // set up
    if(z == null || z.istate == null)
      return Z_STREAM_ERROR;
    if(z.istate.mode != BAD){
      z.istate.mode = BAD;
      z.istate.marker = 0;
    }
    if((n=z.avail_in)==0)
      return Z_BUF_ERROR;
    p=z.next_in_index;
    m=z.istate.marker;

    // search
    while (n!=0 && m < 4){
      if(z.next_in[p] == mark[m]){
        m++;
      }
      else if(z.next_in[p]!=0){
        m = 0;
      }
      else{
        m = 4 - m;
      }
      p++; n--;
    }

    // restore
    z.total_in += p-z.next_in_index;
    z.next_in_index = p;
    z.avail_in = n;
    z.istate.marker = m;

    // return no joy or set up to restart on a new block
    if(m != 4){
      return Z_DATA_ERROR;
    }
    r=z.total_in;  w=z.total_out;
    this.inflateReset(z);
    z.total_in=r;  z.total_out = w;
    z.istate.mode = BLOCKS;
    return Z_OK;
}

  // Returns true if inflate is currently at the end of a block generated
  // by Z_SYNC_FLUSH or Z_FULL_FLUSH. This function is used by one PPP
  // implementation to provide an additional safety check. PPP uses Z_SYNC_FLUSH
  // but removes the length bytes of the resulting empty stored block. When
  // decompressing, PPP checks that at the end of input packet, inflate is
  // waiting for these length bytes.
Inflate.prototype.inflateSyncPoint = function(z){
    if(z == null || z.istate == null || z.istate.blocks == null)
      return Z_STREAM_ERROR;
    return z.istate.blocks.sync_point();
}


//
// InfBlocks.java
//

var INFBLOCKS_BORDER = [16, 17, 18, 0, 8, 7, 9, 6, 10, 5, 11, 4, 12, 3, 13, 2, 14, 1, 15];

function InfBlocks(z, checkfn, w) {
    this.hufts=new Int32Array(MANY*3);
    this.window=new Uint8Array(w);
    this.end=w;
    this.checkfn = checkfn;
    this.mode = IB_TYPE;
    this.reset(z, null);

    this.left = 0;            // if STORED, bytes left to copy 

    this.table = 0;           // table lengths (14 bits) 
    this.index = 0;           // index into blens (or border) 
    this.blens = null;         // bit lengths of codes 
    this.bb=new Int32Array(1); // bit length tree depth 
    this.tb=new Int32Array(1); // bit length decoding tree 

    this.codes = new InfCodes();

    this.last = 0;            // true if this block is the last block 

  // mode independent information 
    this.bitk = 0;            // bits in bit buffer 
    this.bitb = 0;            // bit buffer 
    this.read = 0;            // window read pointer 
    this.write = 0;           // window write pointer 
    this.check = 0;          // check on output 

    this.inftree=new InfTree();
}




InfBlocks.prototype.reset = function(z, c){
    if(c) c[0]=this.check;
    if(this.mode==IB_CODES){
      this.codes.free(z);
    }
    this.mode=IB_TYPE;
    this.bitk=0;
    this.bitb=0;
    this.read=this.write=0;

    if(this.checkfn)
      z.adler=this.check=z._adler.adler32(0, null, 0, 0);
  }

 InfBlocks.prototype.proc = function(z, r){
    var t;              // temporary storage
    var b;              // bit buffer
    var k;              // bits in bit buffer
    var p;              // input data pointer
    var n;              // bytes available there
    var q;              // output window write pointer
    var m;              // bytes to end of window or read pointer

    // copy input/output information to locals (UPDATE macro restores)
    {p=z.next_in_index;n=z.avail_in;b=this.bitb;k=this.bitk;}
    {q=this.write;m=(q<this.read ? this.read-q-1 : this.end-q);}

    // process input based on current state
    while(true){
      switch (this.mode){
      case IB_TYPE:

	while(k<(3)){
	  if(n!=0){
	    r=Z_OK;
	  }
	  else{
	    this.bitb=b; this.bitk=k; 
	    z.avail_in=n;
	    z.total_in+=p-z.next_in_index;z.next_in_index=p;
	    this.write=q;
	    return this.inflate_flush(z,r);
	  };
	  n--;
	  b|=(z.next_in[p++]&0xff)<<k;
	  k+=8;
	}
	t = (b & 7);
	this.last = t & 1;

	switch (t >>> 1){
        case 0:                         // stored 
          {b>>>=(3);k-=(3);}
          t = k & 7;                    // go to byte boundary

          {b>>>=(t);k-=(t);}
          this.mode = IB_LENS;                  // get length of stored block
          break;
        case 1:                         // fixed
          {
              var bl=new Int32Array(1);
	      var bd=new Int32Array(1);
              var tl=[];
	      var td=[];

	      inflate_trees_fixed(bl, bd, tl, td, z);
              this.codes.init(bl[0], bd[0], tl[0], 0, td[0], 0, z);
          }

          {b>>>=(3);k-=(3);}

          this.mode = IB_CODES;
          break;
        case 2:                         // dynamic

          {b>>>=(3);k-=(3);}

          this.mode = IB_TABLE;
          break;
        case 3:                         // illegal

          {b>>>=(3);k-=(3);}
          this.mode = BAD;
          z.msg = "invalid block type";
          r = Z_DATA_ERROR;

	  this.bitb=b; this.bitk=k; 
	  z.avail_in=n;z.total_in+=p-z.next_in_index;z.next_in_index=p;
	  this.write=q;
	  return this.inflate_flush(z,r);
	}
	break;
      case IB_LENS:
	while(k<(32)){
	  if(n!=0){
	    r=Z_OK;
	  }
	  else{
	    this.bitb=b; this.bitk=k; 
	    z.avail_in=n;
	    z.total_in+=p-z.next_in_index;z.next_in_index=p;
	    this.write=q;
	    return this.inflate_flush(z,r);
	  };
	  n--;
	  b|=(z.next_in[p++]&0xff)<<k;
	  k+=8;
	}

	if ((((~b) >>> 16) & 0xffff) != (b & 0xffff)){
	  this.mode = BAD;
	  z.msg = "invalid stored block lengths";
	  r = Z_DATA_ERROR;

	  this.bitb=b; this.bitk=k; 
	  z.avail_in=n;z.total_in+=p-z.next_in_index;z.next_in_index=p;
	  this.write=q;
	  return this.inflate_flush(z,r);
	}
	this.left = (b & 0xffff);
	b = k = 0;                       // dump bits
	this.mode = left!=0 ? IB_STORED : (this.last!=0 ? IB_DRY : IB_TYPE);
	break;
      case IB_STORED:
	if (n == 0){
	  this.bitb=b; this.bitk=k; 
	  z.avail_in=n;z.total_in+=p-z.next_in_index;z.next_in_index=p;
	  write=q;
	  return this.inflate_flush(z,r);
	}

	if(m==0){
	  if(q==end&&read!=0){
	    q=0; m=(q<this.read ? this.read-q-1 : this.end-q);
	  }
	  if(m==0){
	    this.write=q; 
	    r=this.inflate_flush(z,r);
	    q=this.write; m = (q < this.read ? this.read-q-1 : this.end-q);
	    if(q==this.end && this.read != 0){
	      q=0; m = (q < this.read ? this.read-q-1 : this.end-q);
	    }
	    if(m==0){
	      this.bitb=b; this.bitk=k; 
	      z.avail_in=n;z.total_in+=p-z.next_in_index;z.next_in_index=p;
	      this.write=q;
	      return this.inflate_flush(z,r);
	    }
	  }
	}
	r=Z_OK;

	t = this.left;
	if(t>n) t = n;
	if(t>m) t = m;
	arrayCopy(z.next_in, p, window, q, t);
	p += t;  n -= t;
	q += t;  m -= t;
	if ((this.left -= t) != 0)
	  break;
	this.mode = (this.last != 0 ? IB_DRY : IB_TYPE);
	break;
      case IB_TABLE:

	while(k<(14)){
	  if(n!=0){
	    r=Z_OK;
	  }
	  else{
	    this.bitb=b; this.bitk=k; 
	    z.avail_in=n;
	    z.total_in+=p-z.next_in_index;z.next_in_index=p;
	    this.write=q;
	    return this.inflate_flush(z,r);
	  };
	  n--;
	  b|=(z.next_in[p++]&0xff)<<k;
	  k+=8;
	}

	this.table = t = (b & 0x3fff);
	if ((t & 0x1f) > 29 || ((t >> 5) & 0x1f) > 29)
	  {
	    this.mode = IB_BAD;
	    z.msg = "too many length or distance symbols";
	    r = Z_DATA_ERROR;

	    this.bitb=b; this.bitk=k; 
	    z.avail_in=n;z.total_in+=p-z.next_in_index;z.next_in_index=p;
	    this.write=q;
	    return this.inflate_flush(z,r);
	  }
	t = 258 + (t & 0x1f) + ((t >> 5) & 0x1f);
	if(this.blens==null || this.blens.length<t){
	    this.blens=new Int32Array(t);
	}
	else{
	  for(var i=0; i<t; i++){
              this.blens[i]=0;
          }
	}

	{b>>>=(14);k-=(14);}

	this.index = 0;
	mode = IB_BTREE;
      case IB_BTREE:
	while (this.index < 4 + (this.table >>> 10)){
	  while(k<(3)){
	    if(n!=0){
	      r=Z_OK;
	    }
	    else{
	      this.bitb=b; this.bitk=k; 
	      z.avail_in=n;
	      z.total_in+=p-z.next_in_index;z.next_in_index=p;
	      this.write=q;
	      return this.inflate_flush(z,r);
	    };
	    n--;
	    b|=(z.next_in[p++]&0xff)<<k;
	    k+=8;
	  }

	  this.blens[INFBLOCKS_BORDER[this.index++]] = b&7;

	  {b>>>=(3);k-=(3);}
	}

	while(this.index < 19){
	  this.blens[INFBLOCKS_BORDER[this.index++]] = 0;
	}

	this.bb[0] = 7;
	t = this.inftree.inflate_trees_bits(this.blens, this.bb, this.tb, this.hufts, z);
	if (t != Z_OK){
	  r = t;
	  if (r == Z_DATA_ERROR){
	    this.blens=null;
	    this.mode = IB_BAD;
	  }

	  this.bitb=b; this.bitk=k; 
	  z.avail_in=n;z.total_in+=p-z.next_in_index;z.next_in_index=p;
	  write=q;
	  return this.inflate_flush(z,r);
	}

	this.index = 0;
	this.mode = IB_DTREE;
      case IB_DTREE:
	while (true){
	  t = this.table;
	  if(!(this.index < 258 + (t & 0x1f) + ((t >> 5) & 0x1f))){
	    break;
	  }

	  var h; //int[]
	  var i, j, c;

	  t = this.bb[0];

	  while(k<(t)){
	    if(n!=0){
	      r=Z_OK;
	    }
	    else{
	      this.bitb=b; this.bitk=k; 
	      z.avail_in=n;
	      z.total_in+=p-z.next_in_index;z.next_in_index=p;
	      this.write=q;
	      return this.inflate_flush(z,r);
	    };
	    n--;
	    b|=(z.next_in[p++]&0xff)<<k;
	    k+=8;
	  }

//	  if (this.tb[0]==-1){
//            dlog("null...");
//	  }

	  t=this.hufts[(this.tb[0]+(b & inflate_mask[t]))*3+1];
	  c=this.hufts[(this.tb[0]+(b & inflate_mask[t]))*3+2];

	  if (c < 16){
	    b>>>=(t);k-=(t);
	    this.blens[this.index++] = c;
	  }
	  else { // c == 16..18
	    i = c == 18 ? 7 : c - 14;
	    j = c == 18 ? 11 : 3;

	    while(k<(t+i)){
	      if(n!=0){
		r=Z_OK;
	      }
	      else{
		this.bitb=b; this.bitk=k; 
		z.avail_in=n;
		z.total_in+=p-z.next_in_index;z.next_in_index=p;
		this.write=q;
		return this.inflate_flush(z,r);
	      };
	      n--;
	      b|=(z.next_in[p++]&0xff)<<k;
	      k+=8;
	    }

	    b>>>=(t);k-=(t);

	    j += (b & inflate_mask[i]);

	    b>>>=(i);k-=(i);

	    i = this.index;
	    t = this.table;
	    if (i + j > 258 + (t & 0x1f) + ((t >> 5) & 0x1f) ||
		(c == 16 && i < 1)){
	      this.blens=null;
	      this.mode = IB_BAD;
	      z.msg = "invalid bit length repeat";
	      r = Z_DATA_ERROR;

	      this.bitb=b; this.bitk=k; 
	      z.avail_in=n;z.total_in+=p-z.next_in_index;z.next_in_index=p;
	      this.write=q;
	      return this.inflate_flush(z,r);
	    }

	    c = c == 16 ? this.blens[i-1] : 0;
	    do{
	      this.blens[i++] = c;
	    }
	    while (--j!=0);
	    this.index = i;
	  }
	}

	this.tb[0]=-1;
	{
	    var bl=new Int32Array(1);
	    var bd=new Int32Array(1);
	    var tl=new Int32Array(1);
	    var td=new Int32Array(1);
	    bl[0] = 9;         // must be <= 9 for lookahead assumptions
	    bd[0] = 6;         // must be <= 9 for lookahead assumptions

	    t = this.table;
	    t = this.inftree.inflate_trees_dynamic(257 + (t & 0x1f), 
					      1 + ((t >> 5) & 0x1f),
					      this.blens, bl, bd, tl, td, this.hufts, z);

	    if (t != Z_OK){
	        if (t == Z_DATA_ERROR){
	            this.blens=null;
	            this.mode = BAD;
	        }
	        r = t;

	        this.bitb=b; this.bitk=k; 
	        z.avail_in=n;z.total_in+=p-z.next_in_index;z.next_in_index=p;
	        this.write=q;
	        return this.inflate_flush(z,r);
	    }
	    this.codes.init(bl[0], bd[0], this.hufts, tl[0], this.hufts, td[0], z);
	}
	this.mode = IB_CODES;
      case IB_CODES:
	this.bitb=b; this.bitk=k;
	z.avail_in=n; z.total_in+=p-z.next_in_index;z.next_in_index=p;
	this.write=q;

	if ((r = this.codes.proc(this, z, r)) != Z_STREAM_END){
	  return this.inflate_flush(z, r);
	}
	r = Z_OK;
	this.codes.free(z);

	p=z.next_in_index; n=z.avail_in;b=this.bitb;k=this.bitk;
	q=this.write;m = (q < this.read ? this.read-q-1 : this.end-q);

	if (this.last==0){
	  this.mode = IB_TYPE;
	  break;
	}
	this.mode = IB_DRY;
      case IB_DRY:
	this.write=q; 
	r = this.inflate_flush(z, r); 
	q=this.write; m = (q < this.read ? this.read-q-1 : this.end-q);
	if (this.read != this.write){
	  this.bitb=b; this.bitk=k; 
	  z.avail_in=n;z.total_in+=p-z.next_in_index;z.next_in_index=p;
	  this.write=q;
	  return this.inflate_flush(z, r);
	}
	mode = DONE;
      case IB_DONE:
	r = Z_STREAM_END;

	this.bitb=b; this.bitk=k; 
	z.avail_in=n;z.total_in+=p-z.next_in_index;z.next_in_index=p;
	this.write=q;
	return this.inflate_flush(z, r);
      case IB_BAD:
	r = Z_DATA_ERROR;

	this.bitb=b; this.bitk=k; 
	z.avail_in=n;z.total_in+=p-z.next_in_index;z.next_in_index=p;
	this.write=q;
	return this.inflate_flush(z, r);

      default:
	r = Z_STREAM_ERROR;

	this.bitb=b; this.bitk=k; 
	z.avail_in=n;z.total_in+=p-z.next_in_index;z.next_in_index=p;
	this.write=q;
	return this.inflate_flush(z, r);
      }
    }
  }

InfBlocks.prototype.free = function(z){
    this.reset(z, null);
    this.window=null;
    this.hufts=null;
}

InfBlocks.prototype.set_dictionary = function(d, start, n){
    arrayCopy(d, start, window, 0, n);
    this.read = this.write = n;
}

  // Returns true if inflate is currently at the end of a block generated
  // by Z_SYNC_FLUSH or Z_FULL_FLUSH. 
InfBlocks.prototype.sync_point = function(){
    return this.mode == IB_LENS;
}

  // copy as much as possible from the sliding window to the output area
InfBlocks.prototype.inflate_flush = function(z, r){
    var n;
    var p;
    var q;

    // local copies of source and destination pointers
    p = z.next_out_index;
    q = this.read;

    // compute number of bytes to copy as far as end of window
    n = ((q <= this.write ? this.write : this.end) - q);
    if (n > z.avail_out) n = z.avail_out;
    if (n!=0 && r == Z_BUF_ERROR) r = Z_OK;

    // update counters
    z.avail_out -= n;
    z.total_out += n;

    // update check information
    if(this.checkfn != null)
      z.adler=this.check=z._adler.adler32(this.check, this.window, q, n);

    // copy as far as end of window
    arrayCopy(this.window, q, z.next_out, p, n);
    p += n;
    q += n;

    // see if more to copy at beginning of window
    if (q == this.end){
      // wrap pointers
      q = 0;
      if (this.write == this.end)
        this.write = 0;

      // compute bytes to copy
      n = this.write - q;
      if (n > z.avail_out) n = z.avail_out;
      if (n!=0 && r == Z_BUF_ERROR) r = Z_OK;

      // update counters
      z.avail_out -= n;
      z.total_out += n;

      // update check information
      if(this.checkfn != null)
	z.adler=this.check=z._adler.adler32(this.check, this.window, q, n);

      // copy
      arrayCopy(this.window, q, z.next_out, p, n);
      p += n;
      q += n;
    }

    // update pointers
    z.next_out_index = p;
    this.read = q;

    // done
    return r;
  }

//
// InfCodes.java
//

var IC_START=0;  // x: set up for LEN
var IC_LEN=1;    // i: get length/literal/eob next
var IC_LENEXT=2; // i: getting length extra (have base)
var IC_DIST=3;   // i: get distance next
var IC_DISTEXT=4;// i: getting distance extra
var IC_COPY=5;   // o: copying bytes in window, waiting for space
var IC_LIT=6;    // o: got literal, waiting for output space
var IC_WASH=7;   // o: got eob, possibly still output waiting
var IC_END=8;    // x: got eob and all data flushed
var IC_BADCODE=9;// x: got error

function InfCodes() {
}

InfCodes.prototype.init = function(bl, bd, tl, tl_index, td, td_index, z) {
    this.mode=IC_START;
    this.lbits=bl;
    this.dbits=bd;
    this.ltree=tl;
    this.ltree_index=tl_index;
    this.dtree = td;
    this.dtree_index=td_index;
    this.tree=null;
}

InfCodes.prototype.proc = function(s, z, r){ 
    var j;              // temporary storage
    var t;              // temporary pointer (int[])
    var tindex;         // temporary pointer
    var e;              // extra bits or operation
    var b=0;            // bit buffer
    var k=0;            // bits in bit buffer
    var p=0;            // input data pointer
    var n;              // bytes available there
    var q;              // output window write pointer
    var m;              // bytes to end of window or read pointer
    var f;              // pointer to copy strings from

    // copy input/output information to locals (UPDATE macro restores)
    p=z.next_in_index;n=z.avail_in;b=s.bitb;k=s.bitk;
    q=s.write;m=q<s.read?s.read-q-1:s.end-q;

    // process input and output based on current state
    while (true){
      switch (this.mode){
	// waiting for "i:"=input, "o:"=output, "x:"=nothing
      case IC_START:         // x: set up for LEN
	if (m >= 258 && n >= 10){

	  s.bitb=b;s.bitk=k;
	  z.avail_in=n;z.total_in+=p-z.next_in_index;z.next_in_index=p;
	  s.write=q;
	  r = this.inflate_fast(this.lbits, this.dbits, 
			   this.ltree, this.ltree_index, 
			   this.dtree, this.dtree_index,
			   s, z);

	  p=z.next_in_index;n=z.avail_in;b=s.bitb;k=s.bitk;
	  q=s.write;m=q<s.read?s.read-q-1:s.end-q;

	  if (r != Z_OK){
	    this.mode = r == Z_STREAM_END ? IC_WASH : IC_BADCODE;
	    break;
	  }
	}
	this.need = this.lbits;
	this.tree = this.ltree;
	this.tree_index=this.ltree_index;

	this.mode = IC_LEN;
      case IC_LEN:           // i: get length/literal/eob next
	j = this.need;

	while(k<(j)){
	  if(n!=0)r=Z_OK;
	  else{

	    s.bitb=b;s.bitk=k;
	    z.avail_in=n;z.total_in+=p-z.next_in_index;z.next_in_index=p;
	    s.write=q;
	    return s.inflate_flush(z,r);
	  }
	  n--;
	  b|=(z.next_in[p++]&0xff)<<k;
	  k+=8;
	}

	tindex=(this.tree_index+(b&inflate_mask[j]))*3;

	b>>>=(this.tree[tindex+1]);
	k-=(this.tree[tindex+1]);

	e=this.tree[tindex];

	if(e == 0){               // literal
	  this.lit = this.tree[tindex+2];
	  this.mode = IC_LIT;
	  break;
	}
	if((e & 16)!=0 ){          // length
	  this.get = e & 15;
	  this.len = this.tree[tindex+2];
	  this.mode = IC_LENEXT;
	  break;
	}
	if ((e & 64) == 0){        // next table
	  this.need = e;
	  this.tree_index = tindex/3 + this.tree[tindex+2];
	  break;
	}
	if ((e & 32)!=0){               // end of block
	  this.mode = IC_WASH;
	  break;
	}
	this.mode = IC_BADCODE;        // invalid code
	z.msg = "invalid literal/length code";
	r = Z_DATA_ERROR;

	s.bitb=b;s.bitk=k;
	z.avail_in=n;z.total_in+=p-z.next_in_index;z.next_in_index=p;
	s.write=q;
	return s.inflate_flush(z,r);

      case IC_LENEXT:        // i: getting length extra (have base)
	j = this.get;

	while(k<(j)){
	  if(n!=0)r=Z_OK;
	  else{

	    s.bitb=b;s.bitk=k;
	    z.avail_in=n;z.total_in+=p-z.next_in_index;z.next_in_index=p;
	    s.write=q;
	    return s.inflate_flush(z,r);
	  }
	  n--; b|=(z.next_in[p++]&0xff)<<k;
	  k+=8;
	}

	this.len += (b & inflate_mask[j]);

	b>>=j;
	k-=j;

	this.need = this.dbits;
	this.tree = this.dtree;
	this.tree_index = this.dtree_index;
	this.mode = IC_DIST;
      case IC_DIST:          // i: get distance next
	j = this.need;

	while(k<(j)){
	  if(n!=0)r=Z_OK;
	  else{

	    s.bitb=b;s.bitk=k;
	    z.avail_in=n;z.total_in+=p-z.next_in_index;z.next_in_index=p;
	    s.write=q;
	    return s.inflate_flush(z,r);
	  }
	  n--; b|=(z.next_in[p++]&0xff)<<k;
	  k+=8;
	}

	tindex=(this.tree_index+(b & inflate_mask[j]))*3;

	b>>=this.tree[tindex+1];
	k-=this.tree[tindex+1];

	e = (this.tree[tindex]);
	if((e & 16)!=0){               // distance
	  this.get = e & 15;
	  this.dist = this.tree[tindex+2];
	  this.mode = IC_DISTEXT;
	  break;
	}
	if ((e & 64) == 0){        // next table
	  this.need = e;
	  this.tree_index = tindex/3 + this.tree[tindex+2];
	  break;
	}
	this.mode = IC_BADCODE;        // invalid code
	z.msg = "invalid distance code";
	r = Z_DATA_ERROR;

	s.bitb=b;s.bitk=k;
	z.avail_in=n;z.total_in+=p-z.next_in_index;z.next_in_index=p;
	s.write=q;
	return s.inflate_flush(z,r);

      case IC_DISTEXT:       // i: getting distance extra
	j = this.get;

	while(k<(j)){
	  if(n!=0)r=Z_OK;
	  else{

	    s.bitb=b;s.bitk=k;
	    z.avail_in=n;z.total_in+=p-z.next_in_index;z.next_in_index=p;
	    s.write=q;
	    return s.inflate_flush(z,r);
	  }
	  n--; b|=(z.next_in[p++]&0xff)<<k;
	  k+=8;
	}

	this.dist += (b & inflate_mask[j]);

	b>>=j;
	k-=j;

	this.mode = IC_COPY;
      case IC_COPY:          // o: copying bytes in window, waiting for space
        f = q - this.dist;
        while(f < 0){     // modulo window size-"while" instead
          f += s.end;     // of "if" handles invalid distances
	}
	while (this.len!=0){

	  if(m==0){
	    if(q==s.end&&s.read!=0){q=0;m=q<s.read?s.read-q-1:s.end-q;}
	    if(m==0){
	      s.write=q; r=s.inflate_flush(z,r);
	      q=s.write;m=q<s.read?s.read-q-1:s.end-q;

	      if(q==s.end&&s.read!=0){q=0;m=q<s.read?s.read-q-1:s.end-q;}

	      if(m==0){
		s.bitb=b;s.bitk=k;
		z.avail_in=n;z.total_in+=p-z.next_in_index;z.next_in_index=p;
		s.write=q;
		return s.inflate_flush(z,r);
	      }  
	    }
	  }

	  s.window[q++]=s.window[f++]; m--;

	  if (f == s.end)
            f = 0;
	  this.len--;
	}
	this.mode = IC_START;
	break;
      case IC_LIT:           // o: got literal, waiting for output space
	if(m==0){
	  if(q==s.end&&s.read!=0){q=0;m=q<s.read?s.read-q-1:s.end-q;}
	  if(m==0){
	    s.write=q; r=s.inflate_flush(z,r);
	    q=s.write;m=q<s.read?s.read-q-1:s.end-q;

	    if(q==s.end&&s.read!=0){q=0;m=q<s.read?s.read-q-1:s.end-q;}
	    if(m==0){
	      s.bitb=b;s.bitk=k;
	      z.avail_in=n;z.total_in+=p-z.next_in_index;z.next_in_index=p;
	      s.write=q;
	      return s.inflate_flush(z,r);
	    }
	  }
	}
	r=Z_OK;

	s.window[q++]=this.lit; m--;

	this.mode = IC_START;
	break;
      case IC_WASH:           // o: got eob, possibly more output
	if (k > 7){        // return unused byte, if any
	  k -= 8;
	  n++;
	  p--;             // can always return one
	}

	s.write=q; r=s.inflate_flush(z,r);
	q=s.write;m=q<s.read?s.read-q-1:s.end-q;

	if (s.read != s.write){
	  s.bitb=b;s.bitk=k;
	  z.avail_in=n;z.total_in+=p-z.next_in_index;z.next_in_index=p;
	  s.write=q;
	  return s.inflate_flush(z,r);
	}
	this.mode = IC_END;
      case IC_END:
	r = Z_STREAM_END;
	s.bitb=b;s.bitk=k;
	z.avail_in=n;z.total_in+=p-z.next_in_index;z.next_in_index=p;
	s.write=q;
	return s.inflate_flush(z,r);

      case IC_BADCODE:       // x: got error

	r = Z_DATA_ERROR;

	s.bitb=b;s.bitk=k;
	z.avail_in=n;z.total_in+=p-z.next_in_index;z.next_in_index=p;
	s.write=q;
	return s.inflate_flush(z,r);

      default:
	r = Z_STREAM_ERROR;

	s.bitb=b;s.bitk=k;
	z.avail_in=n;z.total_in+=p-z.next_in_index;z.next_in_index=p;
	s.write=q;
	return s.inflate_flush(z,r);
      }
    }
  }

InfCodes.prototype.free = function(z){
    //  ZFREE(z, c);
}

  // Called with number of bytes left to write in window at least 258
  // (the maximum string length) and number of input bytes available
  // at least ten.  The ten bytes are six bytes for the longest length/
  // distance pair plus four bytes for overloading the bit buffer.

InfCodes.prototype.inflate_fast = function(bl, bd, tl, tl_index, td, td_index, s, z) {
    var t;                // temporary pointer
    var   tp;             // temporary pointer (int[])
    var tp_index;         // temporary pointer
    var e;                // extra bits or operation
    var b;                // bit buffer
    var k;                // bits in bit buffer
    var p;                // input data pointer
    var n;                // bytes available there
    var q;                // output window write pointer
    var m;                // bytes to end of window or read pointer
    var ml;               // mask for literal/length tree
    var md;               // mask for distance tree
    var c;                // bytes to copy
    var d;                // distance back to copy from
    var r;                // copy source pointer

    var tp_index_t_3;     // (tp_index+t)*3

    // load input, output, bit values
    p=z.next_in_index;n=z.avail_in;b=s.bitb;k=s.bitk;
    q=s.write;m=q<s.read?s.read-q-1:s.end-q;

    // initialize masks
    ml = inflate_mask[bl];
    md = inflate_mask[bd];

    // do until not enough input or output space for fast loop
    do {                          // assume called with m >= 258 && n >= 10
      // get literal/length code
      while(k<(20)){              // max bits for literal/length code
	n--;
	b|=(z.next_in[p++]&0xff)<<k;k+=8;
      }

      t= b&ml;
      tp=tl; 
      tp_index=tl_index;
      tp_index_t_3=(tp_index+t)*3;
      if ((e = tp[tp_index_t_3]) == 0){
	b>>=(tp[tp_index_t_3+1]); k-=(tp[tp_index_t_3+1]);

	s.window[q++] = tp[tp_index_t_3+2];
	m--;
	continue;
      }
      do {

	b>>=(tp[tp_index_t_3+1]); k-=(tp[tp_index_t_3+1]);

	if((e&16)!=0){
	  e &= 15;
	  c = tp[tp_index_t_3+2] + (b & inflate_mask[e]);

	  b>>=e; k-=e;

	  // decode distance base of block to copy
	  while(k<(15)){           // max bits for distance code
	    n--;
	    b|=(z.next_in[p++]&0xff)<<k;k+=8;
	  }

	  t= b&md;
	  tp=td;
	  tp_index=td_index;
          tp_index_t_3=(tp_index+t)*3;
	  e = tp[tp_index_t_3];

	  do {

	    b>>=(tp[tp_index_t_3+1]); k-=(tp[tp_index_t_3+1]);

	    if((e&16)!=0){
	      // get extra bits to add to distance base
	      e &= 15;
	      while(k<(e)){         // get extra bits (up to 13)
		n--;
		b|=(z.next_in[p++]&0xff)<<k;k+=8;
	      }

	      d = tp[tp_index_t_3+2] + (b&inflate_mask[e]);

	      b>>=(e); k-=(e);

	      // do the copy
	      m -= c;
	      if (q >= d){                // offset before dest
		//  just copy
		r=q-d;
		if(q-r>0 && 2>(q-r)){           
		  s.window[q++]=s.window[r++]; // minimum count is three,
		  s.window[q++]=s.window[r++]; // so unroll loop a little
		  c-=2;
		}
		else{
		  s.window[q++]=s.window[r++]; // minimum count is three,
		  s.window[q++]=s.window[r++]; // so unroll loop a little
		  c-=2;
		}
	      }
	      else{                  // else offset after destination
                r=q-d;
                do{
                  r+=s.end;          // force pointer in window
                }while(r<0);         // covers invalid distances
		e=s.end-r;
		if(c>e){             // if source crosses,
		  c-=e;              // wrapped copy
		  if(q-r>0 && e>(q-r)){           
		    do{s.window[q++] = s.window[r++];}
		    while(--e!=0);
		  }
		  else{
		    arrayCopy(s.window, r, s.window, q, e);
		    q+=e; r+=e; e=0;
		  }
		  r = 0;                  // copy rest from start of window
		}

	      }

	      // copy all or what's left
              do{s.window[q++] = s.window[r++];}
		while(--c!=0);
	      break;
	    }
	    else if((e&64)==0){
	      t+=tp[tp_index_t_3+2];
	      t+=(b&inflate_mask[e]);
	      tp_index_t_3=(tp_index+t)*3;
	      e=tp[tp_index_t_3];
	    }
	    else{
	      z.msg = "invalid distance code";

	      c=z.avail_in-n;c=(k>>3)<c?k>>3:c;n+=c;p-=c;k-=c<<3;

	      s.bitb=b;s.bitk=k;
	      z.avail_in=n;z.total_in+=p-z.next_in_index;z.next_in_index=p;
	      s.write=q;

	      return Z_DATA_ERROR;
	    }
	  }
	  while(true);
	  break;
	}

	if((e&64)==0){
	  t+=tp[tp_index_t_3+2];
	  t+=(b&inflate_mask[e]);
	  tp_index_t_3=(tp_index+t)*3;
	  if((e=tp[tp_index_t_3])==0){

	    b>>=(tp[tp_index_t_3+1]); k-=(tp[tp_index_t_3+1]);

	    s.window[q++]=tp[tp_index_t_3+2];
	    m--;
	    break;
	  }
	}
	else if((e&32)!=0){

	  c=z.avail_in-n;c=(k>>3)<c?k>>3:c;n+=c;p-=c;k-=c<<3;
 
	  s.bitb=b;s.bitk=k;
	  z.avail_in=n;z.total_in+=p-z.next_in_index;z.next_in_index=p;
	  s.write=q;

	  return Z_STREAM_END;
	}
	else{
	  z.msg="invalid literal/length code";

	  c=z.avail_in-n;c=(k>>3)<c?k>>3:c;n+=c;p-=c;k-=c<<3;

	  s.bitb=b;s.bitk=k;
	  z.avail_in=n;z.total_in+=p-z.next_in_index;z.next_in_index=p;
	  s.write=q;

	  return Z_DATA_ERROR;
	}
      } 
      while(true);
    } 
    while(m>=258 && n>= 10);

    // not enough input or output--restore pointers and return
    c=z.avail_in-n;c=(k>>3)<c?k>>3:c;n+=c;p-=c;k-=c<<3;

    s.bitb=b;s.bitk=k;
    z.avail_in=n;z.total_in+=p-z.next_in_index;z.next_in_index=p;
    s.write=q;

    return Z_OK;
}

//
// InfTree.java
//

function InfTree() {
}

InfTree.prototype.huft_build = function(b, bindex, n, s, d, e, t, m, hp, hn, v) {

    // Given a list of code lengths and a maximum table size, make a set of
    // tables to decode that set of codes.  Return Z_OK on success, Z_BUF_ERROR
    // if the given code set is incomplete (the tables are still built in this
    // case), Z_DATA_ERROR if the input is invalid (an over-subscribed set of
    // lengths), or Z_MEM_ERROR if not enough memory.

    var a;                       // counter for codes of length k
    var f;                       // i repeats in table every f entries
    var g;                       // maximum code length
    var h;                       // table level
    var i;                       // counter, current code
    var j;                       // counter
    var k;                       // number of bits in current code
    var l;                       // bits per table (returned in m)
    var mask;                    // (1 << w) - 1, to avoid cc -O bug on HP
    var p;                       // pointer into c[], b[], or v[]
    var q;                       // points to current table
    var w;                       // bits before this table == (l * h)
    var xp;                      // pointer into x
    var y;                       // number of dummy codes added
    var z;                       // number of entries in current table

    // Generate counts for each bit length

    p = 0; i = n;
    do {
      this.c[b[bindex+p]]++; p++; i--;   // assume all entries <= BMAX
    }while(i!=0);

    if(this.c[0] == n){                // null input--all zero length codes
      t[0] = -1;
      m[0] = 0;
      return Z_OK;
    }

    // Find minimum and maximum length, bound *m by those
    l = m[0];
    for (j = 1; j <= BMAX; j++)
      if(this.c[j]!=0) break;
    k = j;                        // minimum code length
    if(l < j){
      l = j;
    }
    for (i = BMAX; i!=0; i--){
      if(this.c[i]!=0) break;
    }
    g = i;                        // maximum code length
    if(l > i){
      l = i;
    }
    m[0] = l;

    // Adjust last length count to fill out codes, if needed
    for (y = 1 << j; j < i; j++, y <<= 1){
      if ((y -= this.c[j]) < 0){
        return Z_DATA_ERROR;
      }
    }
    if ((y -= this.c[i]) < 0){
      return Z_DATA_ERROR;
    }
    this.c[i] += y;

    // Generate starting offsets into the value table for each length
    this.x[1] = j = 0;
    p = 1;  xp = 2;
    while (--i!=0) {                 // note that i == g from above
      this.x[xp] = (j += this.c[p]);
      xp++;
      p++;
    }

    // Make a table of values in order of bit lengths
    i = 0; p = 0;
    do {
      if ((j = b[bindex+p]) != 0){
        this.v[this.x[j]++] = i;
      }
      p++;
    }
    while (++i < n);
    n = this.x[g];                     // set n to length of v

    // Generate the Huffman codes and for each, make the table entries
    this.x[0] = i = 0;                 // first Huffman code is zero
    p = 0;                        // grab values in bit order
    h = -1;                       // no tables yet--level -1
    w = -l;                       // bits decoded == (l * h)
    this.u[0] = 0;                     // just to keep compilers happy
    q = 0;                        // ditto
    z = 0;                        // ditto

    // go through the bit lengths (k already is bits in shortest code)
    for (; k <= g; k++){
      a = this.c[k];
      while (a--!=0){
	// here i is the Huffman code of length k bits for value *p
	// make tables up to required level
        while (k > w + l){
          h++;
          w += l;                 // previous table always l bits
	  // compute minimum size table less than or equal to l bits
          z = g - w;
          z = (z > l) ? l : z;        // table size upper limit
          if((f=1<<(j=k-w))>a+1){     // try a k-w bit table
                                      // too few codes for k-w bit table
            f -= a + 1;               // deduct codes from patterns left
            xp = k;
            if(j < z){
              while (++j < z){        // try smaller tables up to z bits
                if((f <<= 1) <= this.c[++xp])
                  break;              // enough codes to use up j bits
                f -= this.c[xp];           // else deduct codes from patterns
              }
	    }
          }
          z = 1 << j;                 // table entries for j-bit table

	  // allocate new table
          if (this.hn[0] + z > MANY){       // (note: doesn't matter for fixed)
            return Z_DATA_ERROR;       // overflow of MANY
          }
          this.u[h] = q = /*hp+*/ this.hn[0];   // DEBUG
          this.hn[0] += z;
 
	  // connect to last table, if there is one
	  if(h!=0){
            this.x[h]=i;           // save pattern for backing up
            this.r[0]=j;     // bits in this table
            this.r[1]=l;     // bits to dump before this table
            j=i>>>(w - l);
            this.r[2] = (q - this.u[h-1] - j);               // offset to this table
            arrayCopy(this.r, 0, hp, (this.u[h-1]+j)*3, 3); // connect to last table
          }
          else{
            t[0] = q;               // first table is returned result
	  }
        }

	// set up table entry in r
        this.r[1] = (k - w);
        if (p >= n){
          this.r[0] = 128 + 64;      // out of values--invalid code
	}
        else if (v[p] < s){
          this.r[0] = (this.v[p] < 256 ? 0 : 32 + 64);  // 256 is end-of-block
          this.r[2] = this.v[p++];          // simple code is just the value
        }
        else{
          this.r[0]=(e[this.v[p]-s]+16+64); // non-simple--look up in lists
          this.r[2]=d[this.v[p++] - s];
        }

        // fill code-like entries with r
        f=1<<(k-w);
        for (j=i>>>w;j<z;j+=f){
          arrayCopy(this.r, 0, hp, (q+j)*3, 3);
	}

	// backwards increment the k-bit code i
        for (j = 1 << (k - 1); (i & j)!=0; j >>>= 1){
          i ^= j;
	}
        i ^= j;

	// backup over finished tables
        mask = (1 << w) - 1;      // needed on HP, cc -O bug
        while ((i & mask) != this.x[h]){
          h--;                    // don't need to update q
          w -= l;
          mask = (1 << w) - 1;
        }
      }
    }
    // Return Z_BUF_ERROR if we were given an incomplete table
    return y != 0 && g != 1 ? Z_BUF_ERROR : Z_OK;
}

InfTree.prototype.inflate_trees_bits = function(c, bb, tb, hp, z) {
    var result;
    this.initWorkArea(19);
    this.hn[0]=0;
    result = this.huft_build(c, 0, 19, 19, null, null, tb, bb, hp, this.hn, this.v);

    if(result == Z_DATA_ERROR){
      z.msg = "oversubscribed dynamic bit lengths tree";
    }
    else if(result == Z_BUF_ERROR || bb[0] == 0){
      z.msg = "incomplete dynamic bit lengths tree";
      result = Z_DATA_ERROR;
    }
    return result;
}

InfTree.prototype.inflate_trees_dynamic = function(nl, nd, c, bl, bd, tl, td, hp, z) {
    var result;

    // build literal/length tree
    this.initWorkArea(288);
    this.hn[0]=0;
    result = this.huft_build(c, 0, nl, 257, cplens, cplext, tl, bl, hp, this.hn, this.v);
    if (result != Z_OK || bl[0] == 0){
      if(result == Z_DATA_ERROR){
        z.msg = "oversubscribed literal/length tree";
      }
      else if (result != Z_MEM_ERROR){
        z.msg = "incomplete literal/length tree";
        result = Z_DATA_ERROR;
      }
      return result;
    }

    // build distance tree
    this.initWorkArea(288);
    result = this.huft_build(c, nl, nd, 0, cpdist, cpdext, td, bd, hp, this.hn, this.v);

    if (result != Z_OK || (bd[0] == 0 && nl > 257)){
      if (result == Z_DATA_ERROR){
        z.msg = "oversubscribed distance tree";
      }
      else if (result == Z_BUF_ERROR) {
        z.msg = "incomplete distance tree";
        result = Z_DATA_ERROR;
      }
      else if (result != Z_MEM_ERROR){
        z.msg = "empty distance tree with lengths";
        result = Z_DATA_ERROR;
      }
      return result;
    }

    return Z_OK;
}
/*
  static int inflate_trees_fixed(int[] bl,  //literal desired/actual bit depth
                                 int[] bd,  //distance desired/actual bit depth
                                 int[][] tl,//literal/length tree result
                                 int[][] td,//distance tree result 
                                 ZStream z  //for memory allocation
				 ){

*/

function inflate_trees_fixed(bl, bd, tl, td, z) {
    bl[0]=fixed_bl;
    bd[0]=fixed_bd;
    tl[0]=fixed_tl;
    td[0]=fixed_td;
    return Z_OK;
}

InfTree.prototype.initWorkArea = function(vsize){
    if(this.hn==null){
        this.hn=new Int32Array(1);
        this.v=new Int32Array(vsize);
        this.c=new Int32Array(BMAX+1);
        this.r=new Int32Array(3);
        this.u=new Int32Array(BMAX);
        this.x=new Int32Array(BMAX+1);
    }
    if(this.v.length<vsize){ 
        this.v=new Int32Array(vsize); 
    }
    for(var i=0; i<vsize; i++){this.v[i]=0;}
    for(var i=0; i<BMAX+1; i++){this.c[i]=0;}
    for(var i=0; i<3; i++){this.r[i]=0;}
//  for(int i=0; i<BMAX; i++){u[i]=0;}
    arrayCopy(this.c, 0, this.u, 0, BMAX);
//  for(int i=0; i<BMAX+1; i++){x[i]=0;}
    arrayCopy(this.c, 0, this.x, 0, BMAX+1);
}

var testArray = new Uint8Array(1);
var hasSubarray = (typeof testArray.subarray === 'function');
var hasSlice = false; /* (typeof testArray.slice === 'function'); */ // Chrome slice performance is so dire that we're currently not using it...

function arrayCopy(src, srcOffset, dest, destOffset, count) {
    if (count == 0) {
        return;
    } 
    if (!src) {
        throw "Undef src";
    } else if (!dest) {
        throw "Undef dest";
    }

    if (srcOffset == 0 && count == src.length) {
        arrayCopy_fast(src, dest, destOffset);
    } else if (hasSubarray) {
        arrayCopy_fast(src.subarray(srcOffset, srcOffset + count), dest, destOffset); 
    } else if (src.BYTES_PER_ELEMENT == 1 && count > 100) {
        arrayCopy_fast(new Uint8Array(src.buffer, src.byteOffset + srcOffset, count), dest, destOffset);
    } else { 
        arrayCopy_slow(src, srcOffset, dest, destOffset, count);
    }

}

function arrayCopy_slow(src, srcOffset, dest, destOffset, count) {

    // dlog('_slow call: srcOffset=' + srcOffset + '; destOffset=' + destOffset + '; count=' + count);

     for (var i = 0; i < count; ++i) {
        dest[destOffset + i] = src[srcOffset + i];
    }
}

function arrayCopy_fast(src, dest, destOffset) {
    dest.set(src, destOffset);
}


  // largest prime smaller than 65536
var ADLER_BASE=65521; 
  // NMAX is the largest n such that 255n(n+1)/2 + (n+1)(BASE-1) <= 2^32-1
var ADLER_NMAX=5552;

function adler32(adler, /* byte[] */ buf,  index, len){
    if(buf == null){ return 1; }

    var s1=adler&0xffff;
    var s2=(adler>>16)&0xffff;
    var k;

    while(len > 0) {
      k=len<ADLER_NMAX?len:ADLER_NMAX;
      len-=k;
      while(k>=16){
        s1+=buf[index++]&0xff; s2+=s1;
        s1+=buf[index++]&0xff; s2+=s1;
        s1+=buf[index++]&0xff; s2+=s1;
        s1+=buf[index++]&0xff; s2+=s1;
        s1+=buf[index++]&0xff; s2+=s1;
        s1+=buf[index++]&0xff; s2+=s1;
        s1+=buf[index++]&0xff; s2+=s1;
        s1+=buf[index++]&0xff; s2+=s1;
        s1+=buf[index++]&0xff; s2+=s1;
        s1+=buf[index++]&0xff; s2+=s1;
        s1+=buf[index++]&0xff; s2+=s1;
        s1+=buf[index++]&0xff; s2+=s1;
        s1+=buf[index++]&0xff; s2+=s1;
        s1+=buf[index++]&0xff; s2+=s1;
        s1+=buf[index++]&0xff; s2+=s1;
        s1+=buf[index++]&0xff; s2+=s1;
        k-=16;
      }
      if(k!=0){
        do{
          s1+=buf[index++]&0xff; s2+=s1;
        }
        while(--k!=0);
      }
      s1%=ADLER_BASE;
      s2%=ADLER_BASE;
    }
    return (s2<<16)|s1;
}



function jszlib_inflate_buffer(buffer, start, length, afterUncOffset) {
    if (!start) {
        buffer = new Uint8Array(buffer);
    } else {
        buffer = new Uint8Array(buffer, start, length);
    }

    var z = new ZStream();
    z.inflateInit(DEF_WBITS, true);
    z.next_in = buffer;
    z.next_in_index = 0;
    z.avail_in = buffer.length;

    var oBlockList = [];
    var totalSize = 0;
    while (true) {
        var obuf = new Uint8Array(32000);
        z.next_out = obuf;
        z.next_out_index = 0;
        z.avail_out = obuf.length;
        var status = z.inflate(Z_NO_FLUSH);
        if (status != Z_OK && status != Z_STREAM_END) {
            throw z.msg;
        }
        if (z.avail_out != 0) {
            var newob = new Uint8Array(obuf.length - z.avail_out);
            arrayCopy(obuf, 0, newob, 0, (obuf.length - z.avail_out));
            obuf = newob;
        }
        oBlockList.push(obuf);
        totalSize += obuf.length;
        if (status == Z_STREAM_END) {
            break;
        }
    }

    if (afterUncOffset) {
        afterUncOffset[0] = (start || 0) + z.next_in_index;
    }

    if (oBlockList.length == 1) {
        return oBlockList[0].buffer;
    } else {
        var out = new Uint8Array(totalSize);
        var cursor = 0;
        for (var i = 0; i < oBlockList.length; ++i) {
            var b = oBlockList[i];
            arrayCopy(b, 0, out, cursor, b.length);
            cursor += b.length;
        }
        return out.buffer;
    }
}
