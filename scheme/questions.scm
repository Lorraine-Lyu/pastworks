(define (caar x) (car (car x)))
(define (cadr x) (car (cdr x)))
(define (cdar x) (cdr (car x)))
(define (cddr x) (cdr (cdr x)))

; Some utility functions that you may find useful to implement.

(define (cons-all first rests)
  (map (lambda (lst) (cons first lst)) rests))

(define (zip pairs)
  'replace-this-line)

;; Problem 17
;; Returns a list of two-element lists
(define (enumerate s)
  ; BEGIN PROBLEM 17

  (define (helper curr index)
  (if (null? curr) nil
      (cons (cons index (cons (car curr) nil)) (helper (cdr curr) (+ index 1)))
    )
  ) (helper s 0))

  ; END PROBLEM 17

;; Problem 18
;; List all ways to make change for TOTAL with DENOMS
(define (list-change total denoms)
  ; BEGIN PROBLEM 18
  (define (helper total denoms curr)
  (cond ((or (< total 0) (null? denoms)) nil)
        ((= total 0) (list curr))
        (else
          (define left (helper (- total (car denoms)) denoms (append curr (list (car denoms)))))
          (define right (helper total (cdr denoms) curr))

            (append left right)


        )
  )
  )
  (helper total denoms '())
  )
  ; END PROBLEM 18

;; Problem 19
;; Returns a function that checks if an expression is the special form FORM
(define (check-special form)
  (lambda (expr) (equal? form (car expr))))

(define lambda? (check-special 'lambda))
(define define? (check-special 'define))
(define quoted? (check-special 'quote))
(define let?    (check-special 'let))

;; Converts all let special forms in EXPR into equivalent forms using lambda
(define (let-to-lambda expr)
  (cond ((atom? expr)
         ; BEGIN PROBLEM 19
         expr
         ; END PROBLEM 19
         )
        ((quoted? expr)
         ; BEGIN PROBLEM 19
        expr
         ; END PROBLEM 19
         )
        ((or (lambda? expr)
             (define? expr))
         (let ((form   (car expr))
               (params (cadr expr))
               (body   (cddr expr)))
           ; BEGIN PROBLEM 19
           (define params (map let-to-lambda params))
           (define body (map (lambda (x) (let-to-lambda x)) body))

           (define first `(,form ,params))
           (append first body)
           ; END PROBLEM 19
           ))
        ((let? expr)
         (let ((values (cadr expr))
               (body   (cddr expr)))
           ; BEGIN PROBLEM 19
           (define values-formals (map (lambda (x) (let-to-lambda (car x))) values))
           (define values-vals (map (lambda (x) (let-to-lambda (car(cdr x)))) values))
           (define body (let-to-lambda (car body)))
           (define first `(lambda ,values-formals ,body))
           (append (cons first nil) values-vals)
           ; END PROBLEM 19
           ))
        (else
         ; BEGIN PROBLEM 19
         (define form (car expr))
         (define operands (map (lambda (x) (let-to-lambda x)) (cdr expr)))
         (cons form operands)
         ; END PROBLEM 19
         )))
