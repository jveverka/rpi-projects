## Key Event Processing

This chapter describes rules of KeyEvent processing.

![key-event-processing](key-event-processing.svg)

1. Key Event is triggered.  
   1.1 Triggered only by raising edge.  
   1.2 Triggered by leading and falling edge.
2. All instances of the trigger job WAITING in the queue are cancelled.
3. An instance of job is submitted for execution.
4. Stop task is executed, Submit cleanup task.    
   
