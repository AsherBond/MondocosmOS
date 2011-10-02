;;; woksh.el --- WOK TCL interface

;;; Code:

(require 'comint)
(require 'shell)
(require 'wok-comm)

(defvar woksh-program "wish84"
  "*Name of program to invoke woksh")

(defvar woksh-explicit-args "P:/cmd-input.tcl"
  "*List of arguments to pass to woksh on the command line.")

(defvar woksh-mode-hook nil 
  "*Hooks to run after setting current buffer to woksh-mode.")

(defvar woksh-process-connection-type t
  "*If non-`nil', use a pty for the local woksh process.
If `nil', use a pipe (if pipes are supported on the local system).

Generally it is better not to waste ptys on systems which have a static
number of them.  On the other hand, some implementations of `woksh' assume
a pty is being used, and errors will result from using a pipe instead.")

(defvar woksh-directory-tracking-mode 'local
  "*Control whether and how to do directory tracking in an woksh buffer.

nil means don't do directory tracking.

t means do so using an ftp remote file name.

Any other value means do directory tracking using local file names.
This works only if the remote machine and the local one
share the same directories (through NFS).  This is the default.

This variable becomes local to a buffer when set in any fashion for it.

It is better to use the function of the same name to change the behavior of
directory tracking in an woksh session once it has begun, rather than
simply setting this variable, since the function does the necessary
re-synching of directories.")

(make-variable-buffer-local 'woksh-directory-tracking-mode)

;; Initialize woksh mode map.
(defvar woksh-mode-map '())
(cond
 ((null woksh-mode-map)
  (setq woksh-mode-map (if (consp shell-mode-map)
                            (cons 'keymap shell-mode-map)
                          (copy-keymap shell-mode-map)))
  (define-key woksh-mode-map "\C-c\C-c" 'woksh-send-Ctrl-C)
  (define-key woksh-mode-map "\C-c\C-d" 'woksh-send-Ctrl-D)
  (define-key woksh-mode-map "\C-c\C-z" 'woksh-send-Ctrl-Z)
  (define-key woksh-mode-map "\C-c\C-\\" 'woksh-send-Ctrl-backslash)
  (define-key woksh-mode-map "\C-d" 'woksh-delchar-or-send-Ctrl-D)
  (define-key woksh-mode-map "\C-i" 'woksh-tab-or-complete)))

;;(add-hook 'same-window-regexps "^\\*woksh-.*\\*\\(\\|<[0-9]+>\\)")

(defvar woksh-history nil)

(defvar woksh-set-emacs-env nil
"Defines whether modifications in WOK environment variables made,
for instance, by 'wokenv -s' command, should be reflected in Emacs
process environment. 

Default value is nil, i.e. WOK environment changes will not affect 
Emacs variables"
)

(defun woksh-setenv (variable &optional value unset)
"If variable woksh-set-emacs-env is t, calls (setenv) with the 
same arguments, otherwise does nothing."
  (if woksh-set-emacs-env (setenv variable value unset))
)

;;;###autoload
(defun woksh (input-args &optional buffer)
  "Open a woksh"

  (interactive (list
		"1566"
		current-prefix-arg))

  (let* ((process-connection-type woksh-process-connection-type)
         (args nil)
         (buffer-name "*woksh*")
	 (iport (string-to-int input-args))
	 proc)

    (cond ((null buffer))
	  ((stringp buffer)
	   (setq buffer-name buffer))
          ((bufferp buffer)
           (setq buffer-name (buffer-name buffer)))
          ((numberp buffer)
           (setq buffer-name (format "%s<%d>" buffer-name buffer)))
          (t
           (setq buffer-name (generate-new-buffer-name buffer-name))))

    (setq buffer (get-buffer-create buffer-name))
    (pop-to-buffer buffer-name)

    (cond
     ((comint-check-proc buffer-name))
     (t
      (comint-exec buffer buffer-name woksh-program nil args)
      (setq proc (get-buffer-process buffer))
      ;; Set process-mark to point-max in case there is text in the
      ;; buffer from a previous exited process.
      (set-marker (process-mark proc) (point-max))
      (woksh-mode)

      ;; comint-output-filter-functions is just like a hook, except that the
      ;; functions in that list are passed arguments.  add-hook serves well
      ;; enough for modifying it.
      (add-hook 'comint-output-filter-functions 'woksh-carriage-filter)

      (cd-absolute (concat comint-file-name-prefix "~/"))))

    ;; workaround concerning unproper work of tclsh under Emacs on Windows
    (if (equal (getenv "WOKSTATION") "wnt")
      (progn 
        (insert 
           (concat "source " (getenv "WOKHOME") "/site/interp.tcl")) 
        (comint-send-input)))

    (if (not (eq iport 0))
	(if (not  (wok-connectedp))
	    (progn
	      (send-string nil (format "wokemacs_init %d\n" iport))
	      (wok-connect-to-controller "localhost" iport)
	      (send-string nil "auto_load wok_cd_proc\n")
	      (erase-buffer)
	  )))))
 
(defun woksh-mode ()
  "Set major-mode for woksh sessions.
If `woksh-mode-hook' is set, run it."
  (interactive)
  (kill-all-local-variables)
  (shell-mode)
  (setq major-mode 'woksh-mode)
  (setq mode-name "woksh")
  (use-local-map woksh-mode-map)
  (setq shell-dirtrackp woksh-directory-tracking-mode)
  (make-local-variable 'comint-file-name-prefix)
  (run-hooks 'woksh-mode-hook))

(defun woksh-directory-tracking-mode (&optional prefix)
  "Do remote or local directory tracking, or disable entirely.

If called with no prefix argument or a unspecified prefix argument (just
``\\[universal-argument]'' with no number) do remote directory tracking via
ange-ftp.  If called as a function, give it no argument.

If called with a negative prefix argument, disable directory tracking
entirely.

If called with a positive, numeric prefix argument, e.g.
``\\[universal-argument] 1 M-x woksh-directory-tracking-mode\'',
then do directory tracking but assume the remote filesystem is the same as
the local system.  This only works in general if the remote machine and the
local one share the same directories (through NFS)."
  (interactive "P")
  (cond
   ((or (null prefix)
        (consp prefix))
    (setq woksh-directory-tracking-mode t)
    (setq shell-dirtrackp t)
    (setq comint-file-name-prefix ""))
   ((< prefix 0)
    (setq woksh-directory-tracking-mode nil)
    (setq shell-dirtrackp nil))
   (t
    (setq woksh-directory-tracking-mode 'local)
    (setq comint-file-name-prefix "")
    (setq shell-dirtrackp t)))
  (cond
   (shell-dirtrackp
    (let* ((proc (get-buffer-process (current-buffer)))
           (proc-mark (process-mark proc))
           (current-input (buffer-substring proc-mark (point-max)))
           (orig-point (point))
           (offset (and (>= orig-point proc-mark)
                        (- (point-max) orig-point))))
      (unwind-protect
          (progn
            (delete-region proc-mark (point-max))
            (goto-char (point-max))
            (shell-resync-dirs))
        (goto-char proc-mark)
        (insert current-input)
        (if offset
            (goto-char (- (point-max) offset))
          (goto-char orig-point)))))))

;; Parse a line into its constituent parts (words separated by
;; whitespace).  Return a list of the words.
(defun woksh-parse-words (line)
  (let ((list nil)
	(posn 0)
        (match-data (match-data)))
    (while (string-match "[^ \t\n]+" line posn)
      (setq list (cons (substring line (match-beginning 0) (match-end 0))
                       list))
      (setq posn (match-end 0)))
    (store-match-data (match-data))
    (nreverse list)))

(defun woksh-carriage-filter (string)
  (let* ((point-marker (point-marker))
         (end (process-mark (get-buffer-process (current-buffer))))
         (beg (or (and (boundp 'comint-last-output-start)
                       comint-last-output-start)
                  (- end (length string)))))
    (goto-char beg)
    (while (search-forward "\C-m" end t)
      (delete-char -1))
    (goto-char point-marker)))

(defun woksh-send-Ctrl-C ()
  (interactive)
  (send-string nil "\C-c"))

(defun woksh-send-Ctrl-D ()
  (interactive)
  (send-string nil "\C-d"))

(defun woksh-send-Ctrl-Z ()
  (interactive)
  (send-string nil "\C-z"))

(defun woksh-send-Ctrl-backslash ()
  (interactive)
  (send-string nil "\C-\\"))

(defun woksh-delchar-or-send-Ctrl-D (arg)
  "\
Delete ARG characters forward, or send a C-d to process if at end of buffer."
  (interactive "p")
  (if (eobp)
      (woksh-send-Ctrl-D)
    (delete-char arg)))

(defun woksh-tab-or-complete ()
  "Complete file name if doing directory tracking, or just insert TAB."
  (interactive)
  (if woksh-directory-tracking-mode
      (comint-dynamic-complete)
    (insert "\C-i")))
;;

(defun wok-command (command) 
  (interactive (list (read-from-minibuffer "Command : "
					   nil nil nil 'woksh-history)))
  (save-excursion
    
    (if (not (wok-connectedp))
	  (if (equal "yes" (completing-read "WOK not connected: connect ? (yes/no) : "
					    '(("yes") ("no")) nil t
					    '("yes" . 0)  'woksh-history))
	      (woksh "1566" "*woksh*")
	    ))
    
    (if (wok-connectedp)
	(progn
	  (set-buffer "*woksh*")
	  (woksh-parse-words (wok-send-command command)))
      (progn
	(ding)
	(error "Wok controller not connected")))))

;; Goto Entity

(defun wokcd ( userpath ) 
  "\
Moves into a Wok entity"
  (interactive (list (read-from-minibuffer "wokcd : "
				     nil nil nil 'woksh-history)))

  (wok-command (format "wokcd %s" userpath)))


(defun wcd ( Unit )
  (interactive (list (read-from-minibuffer "wcd : "
				     nil nil nil 'woksh-history)))
  (wok-command (format "wokcd %s -PSrc" Unit)))

;;; woksh.el ends here
(defvar woksh-entity-history nil)
(defvar woksh-type-history nil)
(defvar woksh-name-history nil)

(defun wok-dired ( Entity Type )
  (interactive (list 
		(setq myent (completing-read "Entity : " 
				 (mapcar 'list (wok-command (format "Sinfo -N"))) nil nil
				 (cons (car (wok-command "wokinfo -n [wokcd]")) 0) 'woksh-entity-history))
		(completing-read "Type : " 
				 (mapcar 'list (wok-command (format "wokinfo -T %s" myent))) nil nil 
				 '("source" . 0) 'woksh-type-history)))
  ;; insert formatted string into a buffer
  (let ((type Type))
    (if (not (string-match ":" Type))
	(setq type (format "%s:." Type)))
    (set-buffer (dired 
		 (car (wok-command (format "wokinfo -p %s %s\n" type Entity)))))
      
    (rename-buffer (format "%s-%s [%s] (%s)" 
			   (car (wok-command (format "wokinfo -n %s" Entity)))
			   type
			   (car (wok-command (format "wokinfo -N %s" Entity)))
			   (car (wok-command (format "wokinfo -t %s" Entity)))))))

(defun wok-findfile ( Entity Type FileName )
  (interactive (list 
		(setq myent (completing-read "Entity : " 
				 (mapcar 'list (wok-command (format "Sinfo -N"))) nil nil
				 (cons (car (wok-command "wokinfo -n [wokcd]")) 0) 'woksh-entity-history))
		(setq mytype (completing-read "Type : " 
				 (mapcar 'list (wok-command (format "wokinfo -T %s" myent))) nil nil 
				 '("source" . 0) 'woksh-type-history))
		(completing-read "Name : "
				 (mapcar 'list (wok-command (format "uinfo -fT%s %s" mytype myent))) nil nil
				 '("" . 0) 'woksh-name-history)))
  ;; insert formatted string into a buffer
  (set-buffer (find-file 
	       (car (wok-command (format "woklocate -p %s:%s:%s\n" Entity Type FileName)))))
  )

(defun wok-locate (  Entity Type FileName )
  (interactive (list 
		(setq myent (completing-read "Entity : " 
					     (mapcar 'list (wok-command (format "Sinfo -N"))) nil nil
					     (cons (car (wok-command "wokinfo -n [wokcd]")) 0) 'woksh-entity-history))
		(setq mytype (completing-read "Type : " 
					      (mapcar 'list (wok-command (format "wokinfo -T %s" myent))) nil nil 
					      '("source" . 0) 'woksh-type-history))
		(completing-read "Name : "
				 (mapcar 'list (wok-command (format "uinfo -fT%s %s" mytype myent))) nil nil
				 '("" . 0) 'woksh-name-history)))
  ;; insert formatted string into a buffer
  (car (wok-command (format "woklocate -p %s:%s:%s\n" Entity Type FileName)))
  )


(setq wok-compile-defaults '('("umake") ("umake -o obj") ("umake -o exec") ("umake -o xcpp")))

(defun wok-compile ( commande )
  (interactive (list 
		(completing-read "Command : " 
				 wok-compile-defaults nil nil 
			         "umake " 'woksh-history)))
  (set-buffer "*woksh*")
  (wok-command commande))

(defun concat-list-error (thelist)
  (let ((res " "))
    (mapcar (lambda (x)
	      (setq res (concat res x " ")))
	    thelist)
    res))

(defun receive-tcl-error (linearg)
  (interactive)
  
  (kill-buffer (switch-to-buffer-other-window "*compilation*"))
  (switch-to-buffer-other-window "*compilation*")
  (compilation-mode)
  (goto-char (point-max))
  (insert "\n\n")
  (insert-file  linearg)
  (compile-goto-error)
)
