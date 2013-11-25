// Timestamp: 2013-11-25-17:00:26
(function ($) {
    'use strict';

    // From https://developer.mozilla.org/en/JavaScript/Reference/Global_Objects/Array/indexOf
    if (!Array.prototype.indexOf) {
        Array.prototype.indexOf = function (searchElement /*, fromIndex */ ) {
            "use strict";
            if (this == null) {
                throw new TypeError();
            }
            var t = Object(this);
            var len = t.length >>> 0;
            if (len === 0) {
                return -1;
            }
            var n = 0;
            if (arguments.length > 0) {
                n = Number(arguments[1]);
                if (n != n) { // shortcut for verifying if it's NaN
                    n = 0;
                } else if (n != 0 && n != Infinity && n != -Infinity) {
                    n = (n > 0 || -1) * Math.floor(Math.abs(n));
                }
            }
            if (n >= len) {
                return -1;
            }
            var k = n >= 0 ? n : Math.max(len - Math.abs(n), 0);
            for (; k < len; k++) {
                if (k in t && t[k] === searchElement) {
                    return k;
                }
            }
            return -1;
        }
    }

}(jQuery));
(function ($) {
    'use strict';

    if(typeof(window.MPI2) === 'undefined') {
        window.MPI2 = {};
    }

}(jQuery));
(function ($) {
    'use strict';

    if(typeof(MPI2.util) === 'undefined') {
        MPI2.util = {};
    }

    MPI2.util.icon = function (name) {
        return '<img src="https://www.mousephenotype.org/sites/mousephenotype.org/files/icons/' + name + '.png" alt="' + name + '">';
    };

    MPI2.util.spinner = '<img src="https://www.mousephenotype.org/sites/mousephenotype.org/files/ajax-loader.gif" alt="spinner">';

    MPI2.util.showYesOrNo = function (booleanValue) {
        if(booleanValue === true) {
            return '<span class="result yes">' + MPI2.util.icon('tick') + '</span>';
        } else {
            return '<span class="result no">' + MPI2.util.icon('bullet_white') + '</span>';
        }
    };

    MPI2.util.invertReason = function (reason) {
        var inverted = {};
        $.each(reason, function (group, fields) {
            $.each(fields, function (idx, field) {
                inverted[field] = group;
            });
        });
        return inverted;
    };

    MPI2.util.tooltip = function (onElement, content) {
        var tooltip, mouseMoveHandler;
        onElement.addClass('mpi2-tooltip-target');
        onElement.hover(function (hoverEvent) {
            tooltip = $('<p class="mpi2-tooltip">' + content + '</p>');
            tooltip.css('position', 'absolute');
            tooltip.css('display', 'none');

            $('body').append(tooltip);
            tooltip.css('left', hoverEvent.pageX + 'px');
            tooltip.css('top', hoverEvent.pageY + 'px');
            tooltip.fadeIn('fast');

            mouseMoveHandler = function (moveEvent) {
                tooltip.css('left', moveEvent.pageX + 'px');
                tooltip.css('top', moveEvent.pageY + 'px');
            };

            onElement.bind('mousemove', mouseMoveHandler);

        }, function () {
            tooltip.remove();
            tooltip.unbind('mousemove', mouseMoveHandler);
        });
    };

}(jQuery));
(function ($) {
    'use strict';

    MPI2.Search = {
        config: {
            defaultSolrURLs: {
                gene: 'http://ikmc.vm.bytemark.co.uk:8983/solr/gene2/search',
                allele: 'http://ikmc.vm.bytemark.co.uk:8983/solr/allele/search',
                //allele: 'http://ikmc.vm.bytemark.co.uk:8984/solr/allele/search',
                //allele: 'http://deskpro101067.internal.sanger.ac.uk:8983/solr/allele/search',
                parameter: 'http://beta.mousephenotype.org/mi/solr/pipeline/select',
                phenotype: 'http://beta.mousephenotype.org/mi/solr/mp/select'
            }
        }
    };

    if (/(\.|^)mousephenotype.org$/i.test(window.location.hostname)) {
        MPI2.Search.config.defaultSolrURLs.gene = '/bytemark/solr/gene2/search';
        MPI2.Search.config.defaultSolrURLs.allele = '/bytemark/solr/allele/search';
    }

    MPI2.Search.Error = function (message) {
        this.name = "MPI2.Search.Error";
    }

    MPI2.Search.Error.prototype = new Error();
    MPI2.Search.Error.prototype.constructor = MPI2.Search.Error;

    MPI2.Search.solrSearch = function (params) {
        var callback = params.callback, solrURL = params.solrURL, perPage = 10, page = 1, processedSolrParams = {}, defaultSolrParams, nonOverridableSolrParams, docClass = params.docClass;

        if (!solrURL) {
            solrURL = docClass.defaultSolrURL;
        }

        if (typeof(params.page) === 'number') {
            page = params.page;
            if (page < 1) {
                page = 1;
            }
        }

        if (typeof(params.perPage) === 'number') {
            perPage = params.perPage;
            if (perPage < 1) {
                perPage = 1;
            }
        }

        if (params.defaultSolrParams) {
            defaultSolrParams = params.defaultSolrParams;
        }

        nonOverridableSolrParams = {
            start: (page-1) * perPage,
            rows: perPage,
            hl: true,
            wt: 'json'
        };

        processedSolrParams = $.extend({}, defaultSolrParams);
        $.extend(processedSolrParams, params.solrParams);
        $.extend(processedSolrParams, nonOverridableSolrParams);

        $.ajax({
            url: solrURL,
            data: processedSolrParams,
            dataType: 'jsonp',
            jsonp: 'json.wrf',
            success: function (solrResponse) {
                var result = {
                    docs: []
                };

                $.each(solrResponse.response.docs, function (idx, solrDoc) {
                    result.docs.push(new docClass(solrDoc, solrResponse));
                });

                result.perPage = perPage;
                result.page = Math.floor((solrResponse.response.start+1) / result.perPage) + 1;
                result.pages = Math.ceil((solrResponse.response.numFound) / result.perPage);

                if (result.page > result.pages) {
                    result.page = result.pages;
                }

                if (result.pages === 0) {
                    result.pages = 1;
                    result.page = 1;
                }

                result.total = solrResponse.response.numFound;

                result.solrParams = solrResponse.responseHeader.params;
                $.each(['json.wrf', '_', 'wt'], function (idx, name) {
                    delete result.solrParams[name];
                });

                callback.call(undefined, result);
            },
            timeout: 10000,
            error: function (jqXHR, textStatus, errorThrown) {
                if (typeof(params.errorCallback) === 'function') {
                    params.errorCallback.call(undefined);
                }
            }
        });
    };

}(jQuery));
(function ($) {
    'use strict';

    MPI2.Search.GeneDoc = function (solrDoc, entireSolrResponse) {
        var self = this, geneURL;

        self.solrDoc = solrDoc;
        self.markerSymbol = solrDoc.marker_symbol;
        self.mgiAccessionId = solrDoc.mgi_accession_id;

        geneURL = encodeURI('/gene_details?gene_id=' + self.mgiAccessionId);
        self.geneLink = '<a href="' + geneURL + '">' + self.markerSymbol + '</a>';
        self.detailsLink = '<a href="' + geneURL + '">Details</a>';

        self.reason = undefined;

        self.status = solrDoc.latest_project_status;

        if (entireSolrResponse !== undefined && entireSolrResponse.hasOwnProperty('highlighting')) {
            self.highlighting = entireSolrResponse.highlighting[self.mgiAccessionId];
            self.reason = MPI2.Search.GeneDoc.lookupReasonsFor(self.highlighting);
        }

    }; // function MPI2.Search.GeneDoc

    MPI2.Search.GeneDoc.STATUS_NAMES = [
        'Not Assigned for ES Cell Production',
        'Assigned for ES Cell Production',
        'ES Cells Produced',
        'Assigned for Mouse Production and Phenotyping',
        'Mice Produced',
        'Phenotype Data Available'
    ];

    MPI2.Search.GeneDoc.REASONS = MPI2.util.invertReason({
        "Gene": [
            "mgi_accession_id",
            "marker_symbol",
            "marker_name",
            "synonym"
        ],
        "Product": [
            "allele"
        ]
    });

    MPI2.Search.GeneDoc.lookupReasonsFor = function (highlighting) {
        var reasonArray = [];
        $.each(highlighting, function (field) {
            if (MPI2.Search.GeneDoc.REASONS.hasOwnProperty(field)) {
                var reason = MPI2.Search.GeneDoc.REASONS[field];
                if (reasonArray.indexOf(reason) === -1) {
                    reasonArray.push(reason);
                }
            }
        });
        return reasonArray.join(', ');
    };

    MPI2.Search.GeneDoc.defaultSolrURL = MPI2.Search.config.defaultSolrURLs.gene;

}(jQuery));
(function ($) {
    'use strict';

    MPI2.Search.AlleleDoc = function (solrDoc, entireSolrResponse) {
        this.solrDoc = solrDoc;

        if(!solrDoc.allele_type) {
            this.solrDoc.allele_type = '';
        }

        if(!solrDoc.allele_name) {
            this.solrDoc.allele_name = '';
        }
    };

    MPI2.Search.AlleleDoc.defaultSolrURL = MPI2.Search.config.defaultSolrURLs.allele;

}(jQuery));
(function ($) {
    'use strict';

    MPI2.Search.ParameterDoc = function (solrDoc, entireSolrResponse) {
        this.solrDoc = solrDoc;
    };

    MPI2.Search.ParameterDoc.defaultSolrURL = MPI2.Search.config.defaultSolrURLs.parameter;

}(jQuery));
(function ($) {
    'use strict';

    MPI2.Search.PhenotypeDoc = function (solrDoc, entireSolrResponse) {
        this.solrDoc = solrDoc;
    };

    MPI2.Search.PhenotypeDoc.defaultSolrURL = MPI2.Search.config.defaultSolrURLs.phenotype;

}(jQuery));
(function ($) {
    'use strict';

    MPI2.Search.Column = function (options) {
        this.grid = options.grid;
        this.name = options.name;
        this.title = options.title;

        if(typeof(options.cellRenderer) !== 'function') {
            throw new MPI2.Search.Error('cellRenderer must be defined in every column');
        } else {
            this.cellRenderer = options.cellRenderer;
        }

        if(options.thRenderer) {
            this.thRenderer = options.thRenderer;
        }

        if(options.tdRenderer) {
            this.tdRenderer = options.tdRenderer;
        }
    };

    MPI2.Search.Column.prototype.thRenderer = function () {
        return $('<th class="' + this.name + '">' + this.title + '</th>');
    };

    MPI2.Search.Column.prototype.tdRenderer = function (geneDoc) {
        var td = $('<td class="' + this.name + '"></td>');
        td.append(this.cellRenderer.call(this, geneDoc));
        return td;
    };

}(jQuery));
(function ($) {
    'use strict';

    $.widget("MPI2.mpi2SolrGrid", {

        options: {
            solrURL: null,
            tableClass: null,
            beforeRender: null,
            columns: []
        },

        _setOption: function(key, value) {
            switch(key) {
            case 'solrURL':
                this.options.solrURL = value;
                break;
            case 'tableClass':
                this.grid.addClass(value);
                this.options.tableClass = value;
                break;
            case 'beforeRender':
                this.options.beforeRender = value;
                break;
            case 'columns':
                throw new Error('"columns" can only be set on initialization');
                break;
            }

            $.Widget.prototype._setOption.apply(this, arguments);
        },

        _emptySearchData: function () {
            return {
                result: {
                    page: 1,
                    perPage: 0,
                    pages: 1,
                    solrParams: {q: ''},
                    docs: [],
                    total: 0
                },
                explanation: ''
            };
        },

        _addColumns: function (columnOptions) {
            var self = this;

            $.each(columnOptions, function (idx, columnOption) {
                $.extend(columnOption, {grid: self});
                self._columns.push(new MPI2.Search.Column(columnOption));
            });
        },

        _buildColumns: function () {
            var self = this;
            self._addColumns.call(self, self.options.columns);
        },

        _create: function () {
            var self = this;

            if (!self.defaultSolrParams) {
                self.defaultSolrParams = {};
            }

            if (typeof (self.perPage) !== 'number') {
                self.perPage = 10;
            }

            self._columns = [];

            self._buildColumns.call(self);

            self.lastSearchData = self._emptySearchData();

            self.searchInProgress = false;

            self.container = $('<div class="mpi2-grid"></div>');
            self.element.append(self.container);

            self.element.bind('search', function (event, params) {
                self._doSearch(params);
                event.stopPropagation();
                return false;
            });

            self.lastSearchContainer = $(
                '<div class="last-search"><strong class="type"></strong>&emsp;&emsp;&emsp;Last search: <span class="explanation"></span>&emsp;&emsp;&emsp;Total: <span class="total"></span></div>'
            );
            self.container.append(self.lastSearchContainer);
            self.lastSearch = {
                type: self.lastSearchContainer.find('.type'),
                explanation: self.lastSearchContainer.find('.explanation'),
                total: self.lastSearchContainer.find('.total')
            };
            self.lastSearchContainer.update = function () {
                self.lastSearch.explanation.text(self.lastSearchData.explanation);
                self.lastSearch.total.text(self.lastSearchData.result.total);
            };

            self.lastSearch.type.text(self._tableType());

            self.grid = $('<table>' +
                          '  <thead class="rows">' +
                          '    <tr class="row">' +
                          '    </tr>' +
                          '  </thead>' +
                          '  <tbody class="rows">' +
                          '  </tbody>' +
                          '</table>');
            $.each(self._columns, function (idx, column) {
                self.grid.find('thead tr').append(column.thRenderer.call(column));
            });

            self.container.append(self.grid);

            self.paginationContainer = $('<div class="pagination"></div>');
            self.container.append(self.paginationContainer);

            self.prevButton = $('<span class="prev"><a href="#">&laquo; Prev</a><span>&laquo; Prev</span></span>');
            self.prevButton.find('a').bind('click', function () { self.changePage(-1); return false; });
            self.paginationContainer.append(self.prevButton);

            self.pageInfo = $('<span class="page-info" style="margin-left: 1em; margin-right: 1em"><span class="page"></span> / <span class="pages"></span></span>');
            self.paginationContainer.append(self.pageInfo);

            self.nextButton = $('<span class="next"><a href="#">Next &raquo;</a><span>Next &raquo;</span></span>');
            self.nextButton.find('a').bind('click', function () { self.changePage(+1); return false; });
            self.paginationContainer.append(self.nextButton);

            self.paginationContainer.update = function () {
                self.pageInfo.find('.page').text('Page ' + self.lastSearchData.result.page);
                self.pageInfo.find('.pages').text(self.lastSearchData.result.pages);

                if (self.lastSearchData.result.page === 1) {
                    self.prevButton.find('a').hide();
                    self.prevButton.find('span').show();
                } else {
                    self.prevButton.find('a').show();
                    self.prevButton.find('span').hide();
                }

                if (self.lastSearchData.result.page === self.lastSearchData.result.pages) {
                    self.nextButton.find('a').hide();
                    self.nextButton.find('span').show();
                } else {
                    self.nextButton.find('a').show();
                    self.nextButton.find('span').hide();
                }
            };

            self.paginationContainer.update();

            self.footer = $('<div class="footer"></div>');
            self.container.append(self.footer);
        },

        beforeRender: function (docs, continueRendering) {
            continueRendering();
        },

        _doSearch: function (params) {
            var self = this, solrParams, page, explanation;

            if (self.searchInProgress === true) {
                return;
            }

            self.searchInProgress = true;

            solrParams = $.extend({}, params.solrParams);

            if (typeof params.page === 'number') {
                page = params.page;
            } else {
                page = 1;
            }

            if (params.explanation) {
                explanation = params.explanation;
            } else {
                explanation = solrParams.q;
            }

            var searchCallback = function (result) {
                self.lastSearchData.result = result;
                self.lastSearchData.explanation = explanation;

                var docs = result.docs;
                var tbody = self.grid.find('tbody');

                var render = function () {

                    tbody.html('');

                    $.each(docs, function (idx, doc) {
                        var tr = $('<tr class="row"></tr>');
                        $.each(self._columns, function (colidx, column) {
                                tr.append(column.tdRenderer.call(column, doc));
                        });

                        tbody.append(tr);
                    });

                    self.paginationContainer.update();
                    self.lastSearchContainer.update();
                    self.searchInProgress = false;
                    self.element.trigger('afterSearch', [self.element]);
                };

                self.beforeRender.call(self, docs, render);
            };

            if (solrParams.q === '' || solrParams.q === undefined) {
                searchCallback.call(undefined, self._emptySearchData().result);
            } else {
                MPI2.Search.solrSearch({
                    docClass: self._docClass,
                    solrParams: solrParams,
                    solrURL: self.options.solrURL,
                    page: page,
                    perPage: self.perPage,
                    callback: searchCallback,
                    defaultSolrParams: self.defaultSolrParams,
                    errorCallback: function () {
                        searchCallback.call(undefined, self._emptySearchData().result);
                    }
                });
            }
        },

        changePage: function (amount) {
            var self = this;
            self._doSearch({
                solrParams: $.extend(true, {}, self.lastSearchData.result.solrParams),
                explanation: self.lastSearchData.explanation,
                page: self.lastSearchData.result.page + amount
            });
        },

        destroy: function () {
            var self = this;
            $.Widget.prototype.destroy.call(self);
            self.container.remove();
            self.container = null;
        }
    });

}(jQuery));
(function ($) {
    'use strict';

    $.widget("MPI2.mpi2GeneGrid", $.MPI2.mpi2SolrGrid, {

        options: {
            loggedIn: false
        },

        _tableType: function () { return 'Genes'; },

        _create: function () {
            var self = this;

            self._docClass = MPI2.Search.GeneDoc;

            $.MPI2.mpi2SolrGrid.prototype._create.apply(self, arguments);

            self.container.addClass('gene');

            self.footer.append('<a href="/sites/mousephenotype.org/files/impc_gene_list.csv">Download gene list</a>');

            var statusList = $('<ul></ul>');
            $.each(MPI2.Search.GeneDoc.STATUS_NAMES, function (idx, statusName) {
                statusList.append($('<li>' + statusName + '</li>'));
            });
            var statusLegend = $('<p class="statuses"><span>IMPC projects have the following statuses:</span><br></p>');
            statusLegend.append(statusList);
            self.footer.append(statusLegend);
        },

        _doSearch: function () {
            this.element.unbind('registerLinksLoaded');
            $.MPI2.mpi2SolrGrid.prototype._doSearch.apply(this, arguments);
        },

        _loggedInRegisterLinkRenderer: function (grid, geneDoc) {
            var column = this;
            var tempCell = $('<span></span>');

            var handler = function (event) {
                var getLinkText = function () {
                    if(geneDoc.interestRegistered === true) {
                        return "Unregister from status updates";
                    } else {
                        return "Register for status updates";
                    }
                    event.stopPropagation();
                    return false;
                };

                var cell = $('<span></span>');
                var spinner = $(MPI2.util.spinner);
                spinner.hide();
                cell.append(spinner);
                var link = $('<a href="#" data-mgi-acession-id="' + geneDoc.mgiAccessionId + '">' + getLinkText() + '</a>');
                cell.append(link);

                link.bind('click', function () {
                    spinner.show();
                    link.hide();

                    $.ajax({
                        url: '/toggleflagfromjs/' + geneDoc.mgiAccessionId,
                        success: function (response) {
                            if(response === 'null') {
                                window.alert('Error trying to register interest (got null)');
                            } else {
                                if(geneDoc.interestRegistered === true) {
                                    geneDoc.interestRegistered = false;
                                } else {
                                    geneDoc.interestRegistered = true;
                                }
                                link.html(getLinkText());
                            }
                            spinner.hide();
                            link.show();
                        },
                        error: function () {
                            window.alert('Error trying to register interest');
                            spinner.hide();
                            link.show();
                        }
                    });
                    return false;
                });

                cell.hide();
                tempCell.replaceWith(cell);
                cell.fadeIn('slow');
                grid.element.unbind('registerLinksLoaded', handler);
            };
            grid.element.bind('registerLinksLoaded', handler);

            return tempCell;
        },

        _buildLoggedInColumn: function () {
            var self = this;
            return new MPI2.Search.Column({
                grid: self,
                name: 'register',
                title: 'Register Interest',
                cellRenderer: function (geneDoc) { return self._loggedInRegisterLinkRenderer.call(this, self, geneDoc); }
            });
        },

        _buildNotLoggedInColumn: function () {
            var self = this;
            return new MPI2.Search.Column({
                grid: self,
                name: 'register',
                title: 'Register for updates',
                cellRenderer: function (geneDoc) {
                    var link = '/user/login?destination=' + window.location.pathname;
                    return $('<span><a href="' + link + '">Unregistered (register)</a></span>');
                }
            });
        },

        _buildColumns: function () {
            var self = this;

            var options = [
                {
                    name: 'gene',
                    title: 'Gene',
                    cellRenderer: function (geneDoc) {return $('<span>' + geneDoc.geneLink + '</span>');}
                },
                {
                    name: 'latest-status',
                    title: 'Latest Status',
                    cellRenderer: function (geneDoc) {return $('<span>' + geneDoc.status + '</span>');}
                },
                {
                    name: 'reason',
                    title: 'Reason For Match',
                    cellRenderer: function (geneDoc) {
                        var detailsArray = [];
                        $.each(geneDoc.highlighting, function (field, value) {
                            detailsArray.push(field.replace('_', ' ') + ': ' + value.join(", "));
                        });
                        var reasonElement = $('<span>' + geneDoc.reason + '</span>');
                        MPI2.util.tooltip(reasonElement, detailsArray.join("<br/>"));
                        return reasonElement;
                    }
                }
            ];

            $.MPI2.mpi2SolrGrid.prototype._buildColumns.call(self);
            self._addColumns.call(self, options);

            if(self.options.loggedIn) {
                self._columns.push(self._buildLoggedInColumn.call(self));
            } else {
                self._columns.push(self._buildNotLoggedInColumn.call(self));
            }
        },

        beforeRender: function (geneDocs, continueRendering) {
            var self = this;

            if (!self.options.loggedIn) {
                continueRendering();
                return;
            }

            var geneDocsByMgiId = {};
            $.each(geneDocs, function (idx, geneDoc) {
                geneDocsByMgiId[geneDoc.mgiAccessionId] = geneDoc;
            });

            $.ajax({
                url: '/genesofinterest',
                dataType: 'json',
                success: function (response) {
                    if (response && response.hasOwnProperty('MGIGeneid')) {
                        var registeredIds = response['MGIGeneid'];
                        $.each(registeredIds, function (index, registeredId) {
                            if(geneDocsByMgiId.hasOwnProperty(registeredId)) {
                                geneDocsByMgiId[registeredId].interestRegistered = true;
                            }
                        });
                    }
                    self.element.trigger('registerLinksLoaded');
                },
                error: function (jqXHR, textStatus, errorThrown) {
                    window.alert(textStatus + ": " + errorThrown);
                    self.element.trigger('registerLinksLoaded');
                }
            });

            continueRendering();
        }

    }); // $.widget("MPI2.mpi2GeneGrid", $.MPI2.mpi2SolrGrid, {

}(jQuery));
(function ($) {
    'use strict';

    $.widget("MPI2.mpi2ParameterGrid", $.MPI2.mpi2SolrGrid, {

        _tableType: function () { return 'Protocols'; },

        _create: function () {
            var self = this;

            self._docClass = MPI2.Search.ParameterDoc;

            $.MPI2.mpi2SolrGrid.prototype._create.call(self);

            self.container.addClass('parameter');
        },

        _buildColumns: function () {
            var self = this;

            var options = [
                {
                    name: 'parameter',
                    title: 'Parameter',
                    cellRenderer: function (doc) {return $('<span>' + doc.solrDoc.parameter_name + '</span>');}
                },
                {
                    name: 'procedure',
                    title: 'Procedure',
                    cellRenderer: function (doc) {
                        var url = '/impress/impress/displaySOP/' + doc.solrDoc.procedure_stable_key;
                        var link = '<a href="' + url + '" target="_blank">' + doc.solrDoc.procedure_name + '</a>';
                        return $('<span>' + link + '</span>');
                    }
                },
                {
                    name: 'pipelin',
                    title: 'Pipeline',
                    cellRenderer: function (doc) {return $('<span>' + doc.solrDoc.pipeline_name + '</span>');}
                }
            ];

            $.MPI2.mpi2SolrGrid.prototype._buildColumns.call(self);
            self._addColumns.call(self, options);
        }

    });

}(jQuery));
(function ($) {
    'use strict';

    $.widget("MPI2.mpi2PhenotypeGrid", $.MPI2.mpi2SolrGrid, {

        _tableType: function () { return 'Phenotype'; },

        _create: function () {
            var self = this;

            self._docClass = MPI2.Search.PhenotypeDoc;

            $.MPI2.mpi2SolrGrid.prototype._create.call(self);

            self.container.addClass('phenotype');
        },

        _mpTermLinkRenderer: function(grid, doc){

                var baseUrl = document.URL.replace('searchAndFacet','') + '?mpid=';
                var link = $('<a></a>').attr({'target':'_blank', 'href':baseUrl + doc.solrDoc.mp_id}).text(doc.solrDoc.mp_term);
                return $('<span></span>').html(link);
        },

        _buildColumns: function (grid, geneDoc) {
            var self = this;

            var options = [
                {
                    name: 'mp_term',
                    title: 'Phenotype',
                    cellRenderer: function (doc) { return self._mpTermLinkRenderer.call(this, self, doc); }
                },
                {
                    name: 'mp_defnition',
                    title: 'Definition',
                    cellRenderer: function (doc) {return $('<span>' + doc.solrDoc.mp_definition + '</span>');}
                }
            ];

            $.MPI2.mpi2SolrGrid.prototype._buildColumns.call(self);
            self._addColumns.call(self, options);
        }

    });

}(jQuery));
(function ($) {
    'use strict';

    $.widget("MPI2.mpi2GenePageAlleleGrid", $.MPI2.mpi2SolrGrid, {

        options: {
            shouldHideCreKnockIns: true
        },

        _tableType: function () { return 'Alleles'; },

        _create: function () {
            var self = this;

            self.perPage = 100;

            self._docClass = MPI2.Search.AlleleDoc;

            self.defaultSolrParams = {
                bq: 'product_type:"ES Cell"^100 type:mi_attempt^10'
            };

            $.MPI2.mpi2SolrGrid.prototype._create.call(self);

            self._shouldHideRedundantData = true;
            self._shouldHideGeneData = false;

            self.container.addClass('allele');
            self.lastSearchContainer.hide();
            self.paginationContainer.hide();

            self.buttonContainer = $('<div></div>');

            self.toggleButton = $('<button>Show less / Show more</button>');
            self.buttonContainer.append(self.toggleButton);

            self.toggleButton.bind('click', function () {
                self._shouldHideRedundantData = ! self._shouldHideRedundantData;
                self.changePage(0);
            });

            self.grid.before(self.buttonContainer);
        },

        // see http://stackoverflow.com/questions/18082/validate-numbers-in-javascript-isnumeric

        isNumber: function(n) {
            return !isNaN(parseFloat(n)) && isFinite(n);
        },

        _buildColumns: function () {
            var self = this;

            var options = [
                {
                    name: 'product',
                    title: 'Product',
                    cellRenderer: function (doc) {
                        return $('<span>' + doc.solrDoc.product_type + '</span>');
                    },
                    tdRenderer: function (doc) {
                        var colspan = '';

                        if(doc.solrDoc.type !== 'gene' || ! doc.solrDoc.vector_project_ids || doc.solrDoc.vector_project_ids.length < 1) {
                            if(!doc.solrDoc.product_type) {
                                doc.solrDoc.product_type = '';
                                doc.solrDoc.project_ids = [];
                                doc.solrDoc.vector_project_ids = [];
                            }

                            var td = $('<td class="' + this.name + '"></td>');
                            td.append(this.cellRenderer.call(this, doc));
                            return td;
                        }

                        colspan = 'colspan="8"';

                        var html = '';
                        $.each(doc.solrDoc.vector_project_ids, function(index, value) {
                            if(value.length < 1 || ! self.isNumber(value)) {
                                return;
                            }
                            html += '<a href="' + 'http://www.mousephenotype.org/martsearch_ikmc_project/martsearch/ikmc_project/' + value + '" target="_blank">' + value + '</a>' + '&nbsp;';
                        });

                        doc.solrDoc.project_ids = undefined;

                        var marker_symbol = "<a href='https://www.mousephenotype.org/phenotype-archive/search#q=" + doc.solrDoc.marker_symbol + "'>" + doc.solrDoc.marker_symbol + "</a>";

                        //var blurb = html.length > 0 ? "Additional Targeting vectors are available for this gene (" + marker_symbol + ") see links for details: " : '';
                        var blurb = html.length > 0 ? "Additional Targeting vectors are available for this gene - see links for details: " : '';

                        //var td = $('<td ALIGN="center" ' + colspan + '>' + blurb + html + '</td>');
                        var td = $('<td ALIGN="center" style="text-align: center" ' + colspan + '>' + blurb + html + '</td>');
                        return td;
                    }
                },
                {
                    name: 'allele-type',
                    title: 'Allele Type',
                    cellRenderer: function (doc) {return $('<span>' + doc.solrDoc.allele_type + '</span>');}
                },
                {
                    name: 'strain-of-origin',
                    title: 'Strain of Origin',
                    cellRenderer: function (doc) {
                        if (doc.solrDoc.strain) {
                            return $('<span>' + doc.solrDoc.strain + '</span>');
                        } else {
                            return $('<span></span>');
                        }
                    }
                },
                {
                    name: 'mgi-allele-name',
                    title: 'MGI Allele Name',
                    cellRenderer: function (doc) {return $('<span>' + doc.solrDoc.allele_name + '</span>');}
                },
                {
                    name: 'allele-map',
                    title: 'Allele Map',
                    cellRenderer: function (doc) {
                        var url = doc.solrDoc.simple_allele_image_url;
                        if (url) {
                        return $('<a href="' + url + '" target="_blank">' +
                                 '<img width="400" src="' + url + '" alt="allele image" />' +
                                 '</a>');
                        }
                        return '';
                    }
                },
                {
                    name: 'allele-sequence',
                    title: 'Allele Sequence',
                    cellRenderer: function (doc) {
                        if(!doc.solrDoc.genbank_file_url) {
                            return '';
                        }
                        return $('<a href=' + doc.solrDoc.genbank_file_url + '>Genbank file</a>');
                    }
                },
                {
                    name: 'projects',
                    title: 'Product Details',
                    cellRenderer: function(doc) {
                        if (!doc.solrDoc.project_ids || doc.solrDoc.project_ids.length < 1) {
                            return '';
                        }
                        var html = '';
                        $.each(doc.solrDoc.project_ids, function(index, value) {
                            if(value.length < 1) {
                                return;
                            }
                            html += '<a href="' + 'http://www.mousephenotype.org/martsearch_ikmc_project/martsearch/ikmc_project/' + value + '" target="_blank">' + value + '</a>' + '&nbsp;';
                        });
                        return html;
                    }
                },
                {
                    name: 'order',
                    title: 'Order',
                    cellRenderer: function (doc) {
                        var names, urls, solrDoc = doc.solrDoc;

                        if (!jQuery.isArray(solrDoc.order_from_names) || !jQuery.isArray(doc.solrDoc.order_from_urls)) {
                            if (!solrDoc.order_from_name || !solrDoc.order_from_url) {
                                names = [];
                                urls = [];
                            } else {
                                names = [solrDoc.order_from_name];
                                urls = [solrDoc.order_from_url];
                            }
                        } else {
                            names = solrDoc.order_from_names;
                            urls = solrDoc.order_from_urls;
                        }

                        var orderLinks = [];
                        $.each(names, function (index) {
                            var name = names[index], url = urls[index];

                            orderLinks.push('<li><a href="' + url + '">' + name + '</a></li>');
                        });

                        return '<ul>' + orderLinks.join() + '</ul>';
                    }
                }
            ];

            $.MPI2.mpi2SolrGrid.prototype._buildColumns.call(self);
            self._addColumns.call(self, options);
        },

        beforeRender: function (docs, continueRenderingFunc) {
            var self = this;

            self._hideData(docs, {
                shouldHideRedundantData: self._shouldHideRedundantData,
                shouldHideCreKnockIns: self.options.shouldHideCreKnockIns,
                shouldHideGeneData: self._shouldHideGeneData
            });

            self._sortDocs(docs);

            continueRenderingFunc.call(self);
        },

        _hideData: function (docs, options) {
            var self = this;

            var alleleIdsOfMice = [];
            var strainsWithConditionalReadyEsCells = {};
            var docsToKeep = [];

            $.each(docs, function (idx, doc) {
                var solrDoc = doc.solrDoc;
                if (solrDoc.product_type === 'Mouse') {
                    if(alleleIdsOfMice.indexOf(solrDoc.allele_id) === -1) {
                        alleleIdsOfMice.push(solrDoc.allele_id);
                    }
                }

                if (solrDoc.product_type === 'ES Cell' && solrDoc.allele_type === 'Conditional Ready') {
                    strainsWithConditionalReadyEsCells[solrDoc.strain] = true;
                }

                if (solrDoc.allele_type === 'Conditional Ready') {
                    solrDoc.allele_type = 'Knockout First, Reporter-tagged insertion with conditional potential';
                }

                if (solrDoc.allele_type === 'Deletion') {
                    solrDoc.allele_type = 'Reporter-Tagged Deletion';
                }
            });

            $.each(docs, function (idx, doc) {
                var solrDoc = doc.solrDoc, keepDoc = true;

                if (options.shouldHideRedundantData === true) {
                    if (solrDoc.product_type === 'ES Cell' &&
                        solrDoc.allele_type === 'Targeted Non Conditional' &&
                        alleleIdsOfMice.indexOf(solrDoc.allele_id) === -1 &&
                        strainsWithConditionalReadyEsCells[solrDoc.strain] === true) {
                        keepDoc = false;
                    }
                }

                if (options.shouldHideCreKnockIns === true) {
                    if (solrDoc.allele_type === 'Cre Knock In') {
                        keepDoc = false;
                    }
                }

                if(options.shouldHideGeneData === true) {
                    if (solrDoc.type === 'gene') {
                        keepDoc = false;
                    }
                }

                if (keepDoc === true) {
                    docsToKeep.push(doc);
                }
            });

            docs.length = 0;
            $.each(docsToKeep, function (idx, doc) {
                docs.push(doc);
            });
        },

        _sortDocs: function (docs) {
            var priorities = {
                'allele':            1000,
                'mi_attempt':        100,
                'phenotype_attempt': 10,
                'gene':              1
            };
            docs.sort(function (doc1, doc2) {
                var p1 = priorities[doc1.solrDoc.type], p2 = priorities[doc2.solrDoc.type];
                return p2 - p1;
            });
        }

    });

}(jQuery));
(function ($) {
    'use strict';

    $.widget('MPI2.mpi2Search', {

        gridsConfig: {
            gene:          'mpi2GeneGrid',
            parameter:     'mpi2ParameterGrid',
            phenotype:     'mpi2PhenotypeGrid'
        },

        options: {
            loggedIn: false
        },

        _create: function () {
            var self = this;

            $.Widget.prototype._create.call(self);

            self.container = $('<div class="mpi2-search-container"></div>');
            self.element.append(self.container);

            self._initializeGrids.call(self);

            self.element.bind('search', function (event, params) {
                var grid = self.grids[params.type];
                var solrParams = params.solrParams;

                if (!grid || !solrParams) {
                    throw new MPI2.Search.Error, 'invalid params to mpi2Search: must have "type" and "solrParams"';
                }

                grid.trigger('search', [{solrParams: solrParams, explanation: params.explanation}]);
                event.stopPropagation();
                return false;
            });
        },

        _initializeGrids: function () {
            var self = this;

            self.grids = {};

            $.each(self.gridsConfig, function (name, widgetFactory) {
                var grid = $('<div></div>');
                if (! $.isEmptyObject(self.grids) ) { grid.hide(); }
                var gridOptions = {loggedIn: self.options.loggedIn};
                self.grids[name] = grid;
                self.container.append(grid);
                grid[widgetFactory](gridOptions);
                grid.bind('afterSearch', function (event) {
                    $.each(self.grids, function (otherGridName, otherGrid) {
                        if (otherGrid !== grid) {
                            otherGrid.hide();
                        }
                    });
                    grid.show();
                });
            });

            return;

            self.grids.gene = $('<div></div>');
            self.container.append(self.grids.gene);
            self.grids.gene.mpi2GeneGrid({loggedIn: self.options.loggedIn});
            self.grids.gene.bind('afterSearch', function (event) {
                self.grids.parameter.hide();
                self.grids.gene.show();
            });

            self.grids.parameter = $('<div></div>');
            self.grids.parameter.hide();
            self.container.append(self.grids.parameter);
            self.grids.parameter.mpi2ParameterGrid();
            self.grids.parameter.bind('afterSearch', function () {
                self.grids.gene.hide();
                self.grids.parameter.show();
            });
       },

        destroy: function () {
            var self = this;
            $.Widget.prototype.destroy.call(self);
            self.grids.gene.mpi2GeneGrid('destroy');
            self.grids.parameter.mpi2ParameterGrid('destroy');
            self.container.remove();
            self.container = null;
        }

    });
}(jQuery));
(function ($) {
    'use strict';

    if(typeof(window.MPI2) === 'undefined') {
        window.MPI2 = {};
    }

    $.widget("MPI2.mpi2SearchInput", {

        options: {
            target: null,
            placeholder: null
        },

        _simpleSearchOnTarget: function (q) {
            var self = this;
            $(self.options.target).trigger('search', [{type: self.typeSelector[0].value, solrParams: {q: self.input.val()}}]);
        },

        _create: function () {
            var self = this;

            self.container = $('<div></div>');
            this.element.append(self.container);
            self.container.addClass('mpi2-search-input-container');

            self.typeSelector = $('<select><option value="gene">gene</option><option value="parameter">parameter</option><option value="phenotype">phenotype</option></select>');
            self.container.append(self.typeSelector);

            self.input = $('<input type="text" placeholder="' + self.options.placeholder + '"></input>');
            self.container.append(self.input);

            self.input.bind('keyup', function (e) {
                if (e.keyCode === 13) {
                    self._simpleSearchOnTarget();
                }
                return false;
            });

            self.button = $('<button class="search">Search</button>');
            self.container.append(self.button);
            self.button.bind('click', function () {
                self._simpleSearchOnTarget();
            });
        },

        destroy: function () {
            var self = this;
            $.Widget.prototype.destroy.call(self);
            self.container.remove();
            self.container = null;
        }
    });
}(jQuery));
