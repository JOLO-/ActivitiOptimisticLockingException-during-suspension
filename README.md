ActivitiOptimisticLockingException-during-suspension
====================================================

This demo illustrate that we can't suspend async subprocess because Activiti throws ActivitiOptimisticLockingException

What happens in demo:
I launch one main process, from which I lauch async subprocess (by signal event). 

At the moment when service task of async subprocess is in progress I try to suspend main process. Result - ActivitiOptimisticLockingException
