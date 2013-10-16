// File designed to be as "jslint.com clean" as possible

var jmud = {}; // Namespace

(function () {
	"use strict";

	// Constants
	jmud.ENTRY_ID = 'entry';
	jmud.OUTPUT_ID = 'output';
	jmud.POLLING_FREQUENCY = 5000;

	// Global variables
	jmud.user = null;
	jmud.pollingHandle = -1;

	jmud.initPage = function () {
		document.getElementById(jmud.ENTRY_ID).focus();

		jmud.writeOutput('Welcome to ScalaMud!');

		var userHash = self.document.location.hash;
		if (null === userHash || '' === userHash) {
			jmud.user = prompt('Enter your username (append #username to URL to avoid this prompt)');
		} else {
			jmud.user = userHash.substring(1);
		}

		jmud.pollingHandle = window.setInterval(function () { jmud.processEntry(''); }, jmud.POLLING_FREQUENCY);
	};

	jmud.onEntryKeyPress = function (oCtl, oEvent) {
		if (jmud.isEnterKeyPress(oEvent)) {
			// Capture the current text as a command
			var sEntry = oCtl.value;

			// Reset the text entry for the next command
			oCtl.value = '';

			// Process the entrt
			jmud.processEntry(sEntry);
		}
	};

	jmud.isEnterKeyPress = function (oEvent) {
		var keynum;

		if (window.event) { // IE8 and earlier
			keynum = oEvent.keyCode;
		} else if (e.which) { // IE9/Firefox/Chrome/Opera/Safari
			keynum = oEvent.which;
		}

		// Detect ENTER key
		return ('\r' === String.fromCharCode(keynum));
	};

	jmud.processEntry = function (sEntry) {
		var oAjaxRequest, sResponse, sOutput;
		oAjaxRequest = new XMLHttpRequest();

		// Synchronous Ajax for simplicity
		oAjaxRequest.open("POST", "ActionHandler", false);
		oAjaxRequest.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
		oAjaxRequest.send('u=' + jmud.user + '&e=' + encodeURIComponent(sEntry));

		sResponse = oAjaxRequest.responseText;
		if ('' !== sEntry || '' !== sResponse) {
			sOutput = '';
			if ('' !== sEntry) {
				sOutput += '> ' + sEntry;
			}

			if ('' !== sResponse) {
				if ('' !== sOutput) {
					sOutput += '\n';
				}

				sOutput += sResponse;
			}

			// Write both our input and the response to the output area 
			jmud.writeOutput(sOutput);
		}
	};

	jmud.writeOutput = function (sOutput) {
		var oOutput, sPadding;
		oOutput = document.getElementById(jmud.OUTPUT_ID);

		// Get a spacer unles we are the first entry
		sPadding = '\n';
		if (oOutput.value.length === 0) {
			sPadding = '';
		}

		// Append the output to the text area
		oOutput.value += sPadding + sOutput;

		// Scroll the text into view
		oOutput.scrollTop = oOutput.scrollHeight;
	};
}());
