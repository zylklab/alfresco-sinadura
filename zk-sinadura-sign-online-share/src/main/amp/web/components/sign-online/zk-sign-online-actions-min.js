(function() {
	YAHOO.Bubbling
			.fire(
					"registerAction",
					{
						actionName : "onActionZKSignOnline",
						fn : function ZK_onActionZKSignOnline(files) {

                            function log(m) {
                                if (typeof (console) != "undefined") {
                                    console.log(m);
                                }
                            }


							popupLoadingGlobal = Alfresco.util.PopupManager.displayMessage(
							{
							    text: this.msg("zk.message.sign.wait"),
							    spanClass : "wait",
							    displayTime: 5 // segundos
							});

							// selectedFiles
							var selectedFiles;
							if (files.length != undefined) {

								var jsonObjs = [];
								for ( var i = 0; i < files.length; i++) {
									jsonObjs[i] = '{"nodeRef" :"'
											+ files[i].nodeRef
											+ '", "mimetype" : "'
											+ files[i].jsNode.mimetype
											+ '", "displayName" :"'
											+ files[i].displayName + '"}';
								}

								selectedFiles = '{"nodes":['
										+ jsonObjs.join(',') + ']}';

							} else {

								selectedFiles = '{"nodeRef" :"'
										+ files.nodeRef
										+ '", "mimetype" : "'
										+ files.jsNode.mimetype
										+ '", "displayName" :"'
										+ files.displayName + '"}';
							}
							
							// ticket
							Alfresco.util.Ajax.request(
					         {
					            method: Alfresco.util.Ajax.GET,
					            url: Alfresco.constants.PROXY_URI+"/net/zylk/sinadura/cloud/params",
					            successCallback:
					            {
					              	fn: function handleSuccess(data)
									{

										var json = eval('(' + data.serverResponse.responseText + ')');
										var docFiles = [];

										// language
										//------------------
										// obtenemos el locale; primer cookies, luego constante
										var localeCookies = YAHOO.util.Cookie.get("alf_share_locale");
										var localeSettings = Alfresco.constants.JS_LOCALE;
										log('locale cookie: ' + localeCookies);
										log('locale settings: ' + localeSettings);
										var localeSelected = localeSettings;
										if (localeCookies != null && localeCookies != undefined){
											localeSelected = localeCookies;
										}
										
										// propiedades firma
										//--------------------------
										var jsonOb = eval('(' + selectedFiles + ')');
									
										if (jsonOb.nodes == undefined) {
										  	docFiles[0] = jsonOb;	
										} else {			 
										   	docFiles = jsonOb.nodes;
										}

										// tipo de firma para los PDFs
										var SIGN_PDF_SIGNATURE_TYPE = 'PDF'; // PDF o XADES
										var hasMimePdf = false;
										var hasMimeAny = false;

										for (var i = 0; i< docFiles.length; i++) {
							
											if (docFiles[i].mimetype == "application/pdf") {
							       				hasMimePdf = true;
							       			} else {
							       				hasMimeAny = true;
							       			}
										}
										
										if (hasMimePdf && hasMimeAny && SIGN_PDF_SIGNATURE_TYPE == 'PDF') {
											// PDF
											// no se como se lee del bundle de i18n desde aqui, asi q de momento dejo el literal hardcode.
											Alfresco.util.PopupManager.displayMessage({
					                        	text: 'No es posible firmar documentos y PDFs a la vez.'
					                        });
					                        
										} else {
										
											// SINADURA START
											var sinadura = new Sinadura(json.sinaduraCloudUrl);
											sinadura.setOption("locale", localeSelected);

											sinadura.setErrorCallback(function(code, message, status) {
												console.log("Error: " + code + " - " + message + " - " + status);
												alert("Error: " + message);
												sinadura.removeTransaction();
											});
											
											for (var i = 0; i< docFiles.length; i++) {
												
												var docFile = docFiles[i];
												console.log("docFile json: " + JSON.stringify(docFile, null, 3));
												var url = window.location.protocol + "//" + window.location.host + "/alfresco/service/api/node/content/" + docFile.nodeRef.replace(":/", "") + "/" + encodeURIComponent(docFile.displayName) +"?a=true&alf_ticket="+json.ticket;
												sinadura.addDocument(docFile.nodeRef, "url", docFile.displayName, url);
											}
											
											sinadura.sign(function(documents) {

												console.log("token: " + sinadura.getToken());
												var token = sinadura.getToken();
												
												console.log("json: " + JSON.stringify(documents, null, 3));
												
												var dfdDocumentSignedArray = [];
												
												// TODO En vez de hacer una peticion por cada documento, habria que hacer una unica peticion (al servicio de Alfresco),
												// ya que ahora se pueden obtener todas las firmas directamente en el servidor.
												// Asi se haria ademas todo el proceso en la misma transacion.
												for (var i = 0; i< documents.length; i++) {
													
													var nodeRef = documents[i].id;
													console.log("doc nodeRef: " + nodeRef);
													
													var urlPost = window.location.protocol + "//" + window.location.host + "/alfresco/service/net/zylk/sinadura/upload/content?content&nodeRef="+nodeRef+"&token="+token+"&alf_ticket="+json.ticket;
												
													console.log("urlPost: " + urlPost);
													
													var dfdDocumentSigned = jQuery.post(urlPost, {'token': token}, function(response) {
														
														console.log("sign response ok: " + response);
														
													}).fail(function(xhr, textStatus, errorThrown) {
														console.log("error: " + xhr + " - " + textStatus + " - " + errorThrown);
												    });
													
													dfdDocumentSignedArray.push(dfdDocumentSigned);
												}
												
												jQuery.when.apply(this, dfdDocumentSignedArray).then(function() {
													// ok en todas las firmas
													console.log("sign complete success");
													sinadura.removeTransaction();
													window.location.reload();
												}, 
												function() {
													alert("Se ha producido un error inesperado al firmar los documentos");
												});
												
											});
											
											// SINADURA END
										}

									},
					              scope: this
					            },
					          failureMessage: "Error al obtener el ticket"
					        });
					
						}
					});
})();