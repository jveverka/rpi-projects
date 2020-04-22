## Jobs, Tasks and Actions

* __job__ - is abstract definition if task, one job consists one or more tasks.
* __action__ - is simple action step executed as part of job.
* __task__ - an instance of job submitted for execution.

### Task Execution
__rpi-powercontroller__ is executing submitted jobs in serial manner and order.
Submitted job is transformed into task with unique task-id and becomes part of job queue execution.
All tasks are executed on single thread. 

#### Submit new Task
* submit task by jobId - TBD
* submit parametric task by jobId - TBD/WIP 

#### List Task Queue
TBD

#### Cancel Submitted Task
TBD

#### Default Jobs
* __kill-all-tasks__ - this task currently executed task as well as all tasks waiting in the execution queue.  

#### Default Actions
* __action-port-high__ - turns ON selected port.
* __action-port-low__ - turns OFF selected port.
* __action-wait__ - waits idle for given time period.

#### Custom Actions
TBD/WIP
