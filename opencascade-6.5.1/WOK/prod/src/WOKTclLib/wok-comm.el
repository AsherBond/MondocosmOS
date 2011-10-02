;;; Communication and interface routines for the WOK and Emacs comm

(require 'cl)
(provide 'wok-comm)

;;; Variables

(defconst wok-comm-AtFS-Header
  "$Header: /disk4/QA/cvsroot/test/ros/src/WOKTclLib/wok-comm.el,v 1.4 2009-12-16 08:40:00 mnt Exp $")

(defvar wok-comm-initialized nil
  "If non-nil, the Wok communication module has already been initialized.")

(defvar wok-log-communication t
  "If non-nil, the communication between Emacs and the Wok widget is
logged in wok-log-buffer.")

(defvar wok-log-buffer-name " *wok-log*"
  "Name of the buffer where Wok communication is logged.
Begins with a blank to be invisible.")

(defvar wok-log-buffer nil
  "Buffer where Wok communication is logged.
If it gets killed, it will be re-created on demand.")

(defvar wok-controller-input-buffer-name " *wok-input*"
  "Name of buffer containing incoming characters, not yet processed.")

(defvar wok-controller-input-queue ""
  "Incoming lines that have not yet been processed.")

(defvar wok-controller-return-buffer-name " *wok-return*"
  "Name of buffer containing returned characters, not yet processed.")

(defvar wok-controller-return-queue ""
  "return value lines that have not yet been processed.")

(defvar wok-controller-process nil
  "Process variable of wok-controller process
(really a network connection).")

(defvar wok-controller-host nil
  "Hostname of remote wok-controller.")

(defvar wok-controller-port nil
  "Port number of remote wok-controller.")

(defvar wok-controller-connectedp nil
  "t if connected otherwise nil.")

(defvar wok-write-back-eval t
  "If non-nil, write results of evaluations back to the wok-controller.")

(defvar wok-widget-name "dummy-widget"
  "Name of the widget we talk to in the remote wok-controller.")

(defvar wok-kill-widget-on-exit nil
  "If non-nil, Emacs kills the widget on exit.")

(defvar wok-default-port "1563"
  "Default port number for connecting to the controller,
if not given in the command line. Mostly for testing purposes.
Must be a string because it is used as initial input for read-string.")

(defvar wok-signal-errors t
  "If non-nil, signal errors in the process-filter to the user.
If nil, rely on the widget to process the error.")


;;; Functions for handling wok-controller-input-buffer

(defun wok-erase-input-buffer ()
  "Erase wok-controller-input-buffer, i.e. flush all input."
  (save-excursion
    (set-buffer (get-buffer-create wok-controller-input-buffer-name))
    (erase-buffer)))


(defun wok-queue-controller-input (string)
  "Add input STRING to wok-controller-input-buffer."
  (save-excursion
    (set-buffer (get-buffer-create wok-controller-input-buffer-name))
    (goto-char (point-max))
    (insert string)))


(defun wok-complete-input-line-p ()
  "Return non-nil if a complete line is available in
wok-controller-input-buffer."
  (save-excursion
    (set-buffer (get-buffer-create wok-controller-input-buffer-name))
    ;; DEBUG POUR DEC/JGAJGA
    ;;(goto-char 1)			; don' bother with point-min here
    ;;(and (> (length (buffer-string)) 0) ( equal "\^J" (buffer-substring 1 2))
    ;;	(delete-char 1)
    ;;  )
    (wok-log-to-buffer "buffer" (buffer-string))
    (goto-char 1)			; don' bother with point-min here
    (forward-line 1)
    (and (bolp)
	 (not (bobp)))))


(defun wok-get-input-line ()
  "Return the first line from wok-controller-input-buffer and erase it there."
  (save-excursion
    (set-buffer (get-buffer-create wok-controller-input-buffer-name))
    (goto-char 1)
    (let ((end (progn (forward-line 1)
		      (point))))
      (prog1
	  (buffer-substring 1 end)
	(delete-region 1 end)))))

;;; Functions for handling wok-controller-return-buffer

(defun wok-erase-return-buffer ()
  "Erase wok-controller-input-buffer, i.e. flush all input."
  (save-excursion
    (set-buffer (get-buffer-create wok-controller-return-buffer-name))
    (erase-buffer)))


(defun wok-queue-controller-return (string)
  "Add input STRING to wok-controller-input-buffer."
  (save-excursion
    (set-buffer (get-buffer-create wok-controller-return-buffer-name))
    (goto-char (point-max))
    (insert string)))


(defun wok-complete-return-line-p ()
  "Return non-nil if a complete line is available in
wok-controller-return-buffer."
  (save-excursion
    (set-buffer (get-buffer-create wok-controller-return-buffer-name))
    (goto-char 1)			; don' bother with point-min here
    (forward-line 1)
    (and (bolp)
	 (not (bobp)))))


(defun wok-get-return-line ()
  "Return the first line from wok-controller-input-buffer and erase it there."
  (save-excursion
    (set-buffer (get-buffer-create wok-controller-return-buffer-name))
    (goto-char 1)
    (let ((end (progn (forward-line 1)
		      (point))))
      (prog1
	  (buffer-substring 1 end)
	(delete-region 1 end)))))

;;; Functions etc. to set up, continue, and shut down communication
;;; to the WokEmacs widget

(defun wok-get-command-line-args (switch)
  "Consume commandline arguments after \"-wokwidget\" and connect
to remote wok-controller. Arguments are:
  - wok-widget-name
  - wok-controller-host
  - wok-controller-port"
  (if (equal switch "-wokwidget")
      (progn
	(setq wok-widget-name
	      (car command-line-args-left))
	(setq wok-controller-host
	      (cadr command-line-args-left))
	(setq wok-controller-port
	      (string-to-int (caddr command-line-args-left)))
	(setq command-line-args-left
	      (cdddr command-line-args-left))
	(wok-connect-to-controller wok-controller-host wok-controller-port))))


(defun wok-connect-to-controller (host port)
  "Establish a connection to a remote wok-controller on HOST port PORT.
This function is a command only for testing purposes."
  (interactive (list (read-string "To host: " "localhost")
		     (string-to-int (read-string "Port: "
						 wok-default-port))))
  (if wok-controller-process
      ;; there must not be two controllers
      (error "Wok-Controller already running on host %s port %s"
	     wok-controller-host wok-controller-port)
    ;; set up process and associated variables
    (progn
      (message "trying connection to host %s port %s"
	       wok-controller-host wok-controller-port)
      (let ((retries 0))
	(while (and (not wok-controller-process) (not (equal retries 8)))
	  (condition-case error (progn 
				  (setq wok-controller-process
					(open-network-stream "wok-controller-process" nil host port))
				  t)
	    (error
	     (progn 
	       (message "Retry %d failed" retries)
	       (sleep-for 1)
	       (setq retries (+ retries 1))
	       (let ((mesg (car (cdr error))))
		 (cond
		  ((string-match "^Unknown host" mesg) nil)
		  ((string-match "not responding$" mesg) mesg)
		  ((equal mesg "connection failed")
		   (if (equal (nth 2 error) "permission denied")
		       nil			; host does not exist
		     (nth 2 error)))
		  ;; Could be "Unknown service":
		  (setq retries (+ retries 1))
		  (t (signal (car error) (cdr error))))))))))

      (message "Connection established.")
      
      (if wok-controller-process
	  (progn
	    (setq wok-controller-host host)
	    (setq wok-controller-port port)
	    (wok-erase-input-buffer)
	    (wok-erase-return-buffer)
	    (set-process-filter   wok-controller-process 'wok-controller-filter)
	    (set-process-sentinel wok-controller-process 'wok-shutdown-controller)
	    ;; first handshake
	    (wok-send-return-value "Hello widget, pleased to meet you!")
      
	    (run-hooks 'wok-connect-hooks)
	    
	    (setq wok-controler-connectedp t)
	    wok-controller-process)))))

(defun wok-shutdown-controller (&optional proc message)
  "Sentinel for the connection to a remote wok-controller.
This is a command only for testing purposes.
Since the only status change is connection loss, the only action to
be done is cleaning up.
Optional arguments: PROC MESSAGE.
If PROC is nil, no message is given."
  (interactive "p")
  (if proc
      (message "Wok-Controller on %s port %d shutdown."
	       wok-controller-host wok-controller-port))
  (condition-case dummy
      ;; this closes the network connection. Errors must be ignored,
      ;; because the connection will already be closed if this
      ;; function is called as the process sentinel.
      (delete-process wok-controller-process)
    (error nil))
  ;; reset associated variables
  (setq wok-controller-process nil)
  (setq wok-controller-port nil)
  (setq wok-controller-host nil)
  (setq wok-controler-connectedp nil)
  (if proc
      ;; proc is non-nil if this function has been called as the
      ;; sentinel or if the user wants it.
      (ding)))


(defun wok-controller-filter (proc string)
  "Filter for the connection to a remote wok-controller.
It relies on the messages coming in line by line,
perhaps this is a bug, we'll see."

  ;; log to buffer if requested
  (wok-log-to-buffer "recv" string)
  ;; collect a complete line first
  (wok-queue-controller-input string)
  ;; line complete?
  (wok-log-to-buffer "input-line-p" (wok-complete-input-line-p))

  (while (wok-complete-input-line-p)
      (condition-case error-message
	  (let ((line (wok-get-input-line)))
	    (wok-log-to-buffer "line" line)
	    (if (< (length line) 5)	; including newline character
		;; line is too short
		(progn (wok-erase-input-buffer)
		       (wok-raise-error "line too short"))
	      ;; The message type tokens are four characters long
	      (let ((token (substring line 0 4)))
		;; switch according to the type of token. 
		(cond ((equal token "RST:")
		       ;; reset communication. All input is flushed.
		       (setq wok-controller-input-queue "")
		       (wok-erase-input-buffer))
		      
		      ((equal token "CMD:")
		       ;; command lines are collected in
		       ;; wok-controller-input-queue 
		       (setq wok-controller-input-queue
			     (concat wok-controller-input-queue
				     (substring line 5))))
		      
		      ((equal token "END:")
		       (cond  ((> (length wok-controller-input-queue) 0)
			       ;; end of command. Now the command in
			       ;; wok-controller-input-queue can be executed
			       (let ((exp (read wok-controller-input-queue)) value)
				 ;; it is important to clear the input queue
				 ;; immmediately since the filter can be
				 ;; invoked in parallel
				 (setq wok-controller-input-queue "")
				 (setq value (eval exp))
				 ;; write result back only if requested
				 (if wok-write-back-eval
				     (wok-send-return-value value))
				 ))
			      ((> (length wok-controller-return-queue) 0)
			       (progn 
				 (setq wok-return-value wok-controller-return-queue)
				 (setq wok-controller-return-queue "")
				 (setq wok-return-value-p 1)
				 ))
			     ;; (t
			     ;;  (wok-raise-error "Mixed RET: and CMD: tokens"))
			 ))

		      ((equal token "RET:")
		       ;; should not appear (yet). Will perhaps later be
		       ;; needed.
		       (setq wok-controller-return-queue
			     (concat wok-controller-return-queue
				     (substring line 5))))
		      
		      ((equal token "ERR:")
		       ;; is error message from Wok widget
		       (progn  
			 (setq wok-return-value "ERR:ERR")
			 (setq wok-error-msg    (substring line 5 ))
			 (ding)
			 (message "Wok error: %s"
			 	  (substring line 5 ))
			 (setq wok-return-value-p 1)
			 )
		       )
		      
		      (t
		       ;; an unrecognized token occurred
		       (wok-raise-error (format "protocol error, token \"%s\""
					       (substring string 0 4)))
		       (setq wok-controller-input-queue "")))
		;; reset input line, this one has been processed.
		(setq line ""))))
	(quit
	 (progn (setq wok-controller-input-queue "")
		(wok-erase-input-buffer)
		(ding)
		(message "Quit")
		(wok-raise-error error-message)))
	(error
	 ;; any error is given to controller
	 (progn (setq wok-controller-input-queue "")
		(wok-raise-error error-message)
		(if wok-signal-errors
		    (signal (car error-message) (cdr error-message))))))))

;;; Functions to synchronize termination and redisplay

(defun wok-kill-emacs ()
  "Function for Wok to shut down the WokEmacs widget.
For some unknown reason this must include the action which is already
placed in kill-emacs-hook."
  (if wok-controller-process
      (wok-shutdown-controller))
  ;; this must not happen twice, so reset kill-emacs-hook
  (setq kill-emacs-hook nil)
  (kill-emacs))


(defun wok-advise-destroy-widget-on-exit ()
  "Advise Emacs to destroy the widget on exit. Does not yet work.
This is necessary for XfEmacs."
  (setq wok-kill-widget-on-exit t))


(defun wok-kill-emacs-hook ()
  "To be put into kill-emacs-hook in order to guarantee proper
shutdown of the WokEmacs widget."
  ;; notify widget of Emacs' death
  (if wok-kill-widget-on-exit
    (if wok-controller-process
      (wok-send-command (format "%s stopemacs; catch {destroy .}; catch {exit 0}" wok-widget-name))))
  (wok-shutdown-controller))


;;; Functions to implement the protocol between Emacs and the widget

(defun wok-send-command (string)
  "Send STRING as Tcl command to remote wok-controller."
  (interactive "sSend Tcl command: ")
  (progn
    (setq wok-error-msg      nil)
    (setq wok-return-value   "")
    (setq wok-return-value-p 0)
    (wok-send-raw-string (wok-string-format 'cmd "%s" string))
    (while (not (= wok-return-value-p 1))  (sit-for .1))
    (if (equal wok-return-value "ERR:ERR")
	nil
      wok-return-value)
    ))


(defun wok-send-return-value (object)
  "Send OBJECT as a return value to the wok-controller.
OBJECT can be any Lisp object."
  (wok-send-raw-string (wok-string-format 'ret "%s" object)))


(defun wok-raise-error (message)
  "Send MESSAGE as an error message to the wok-controller."
  (condition-case dummy
      (wok-send-raw-string (wok-string-format 'err "%s" message))
    (error nil)))


(defun wok-send-raw-string (string)
  "Send STRING to remote wok-controller. Do not use this
unless you really know what you are doing, since this function
lies below the protocol between Emacs and the wok-controller."
  (if wok-controller-process
      (progn (process-send-string wok-controller-process string)
	     (wok-log-to-buffer "send" string))))


(defun wok-string-format (type &rest format-args)
  "Format string suitable for transmission to the wok widget.
TYPE may be 'ret or 'cmd or 'err, remaining FORMAT-ARGS are
processed by format."
  (let ((tmp-buffer (get-buffer-create " *wok-temp-send*"))
	(header (cdr (or (assoc type '((ret . "RET: ")
				       (cmd . "CMD: ")
				       (rst . "RST: ")
				       (err . "ERR: ")))
			 (error "Wrong TYPE parameter %s" type)))))
    (save-excursion
      ;; insert formatted string into a buffer
      (set-buffer tmp-buffer)
      (erase-buffer)
      (insert (apply 'format format-args))
      (goto-char 0)
      ;; insert appropriate header at line beginnings
      (while (not (eobp))
	(insert header)
	(forward-line 1))
      ;; if the last char was a newline, another header is needed
      (if (bolp)
	  (insert header))
      (insert (if (eq type 'err)
		  "\n"
		"\nEND:\n"))
      ;; return string
      (buffer-string))))


(defun wok-log-to-buffer (where string)
  "Log communication to buffer, if wok-log-communication is non-nil.
WHERE is \"recv\" or \"send\", STRING is the message.
See also wok-log-buffer and wok-log-buffer-name."
  (if wok-log-communication
      (save-excursion
	(set-buffer (get-buffer-create wok-log-buffer-name))
	(goto-char (point-max))
	(insert (format "##%s: %s##\n" where string)))))

;;; init

(defun wok-connectedp ()
  (if wok-controller-process
      t
    nil)
  )
    

(defun wok-initialize-communication ()
  "Initialize certain variables and functions for the communication
with the WokEmacs widget."
  (if (not wok-comm-initialized)
      (progn
	;; the "-wokwidget" switch must be parsed by wok-get-command-line-args
	(setq command-switch-alist
	      (cons '("-wokwidget" . wok-get-command-line-args)
		    command-switch-alist))
	(setq wok-comm-initialized t)
	(run-hooks 'wok-initialization-hooks))))


;;; end of file



(wok-initialize-communication)
