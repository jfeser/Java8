package edu.rice.cs.plt.concurrent;

import edu.rice.cs.plt.lambda.Thunk;
import edu.rice.cs.plt.lambda.WrappedException;

/**
 * Provides access to a concurrent task that produces a value.  Implementations may concurrently modify
 * the controller's status, but clients should not assume thread safety (for example, problems may occur
 * if one thread invokes {@link #cancel} while another performs operations that assume the task is not
 * cancelled).
 */
public abstract class TaskController<R> implements Thunk<R> {
  
  /** Current status.  May be safely accessed across multiple threads. */
  protected volatile Status _status;
  
  public TaskController() { _status = Status.PAUSED; }
  
  /** Get the current status. */
  public Status status() { return _status; }
  
  /**
   * Request that the task be run.  If the task is {@code RUNNING} or {@code FINISHED}, has no effect.
   * @throws IllegalStateException  If the task is {@code CANCELLED}.
   */
  public void start() {
    switch (_status) {
      case PAUSED: doStart(); break;
      case CANCELLED: throw new IllegalStateException("Task is cancelled");
    }
  }
  
  /**
   * Request that the task be abandoned, and that any resources associated with it be disposed.  Note 
   * that if the task is {@code RUNNING}, it may not be possible to dispose of it until after it finishes.
   */
  public void cancel() {
    if (_status != Status.CANCELLED) { doCancel(); }
  }
  
  /**
   * Get the result.  If the task is not {@code FINISHED}, request that the task be {@code RUNNING}
   * and block until it finishes.
   * @throws IllegalStateException  If the task is {@code CANCELLED}.
   * @throws WrappedException  Wraps an {@link InterruptedException} if the current thread is interrupted
   *                           while waiting for the result, an {@link java.lang.reflect.InvocationTargetException} 
   *                           containing any error that was triggered while executing the task, or any other 
   *                           exception related to the implementation of the controller.
   */
  public R value() {
    if (_status != Status.CANCELLED) {
      try { return getValue(); }
      catch (Exception e) { throw new WrappedException(e); }
    }
    else { throw new IllegalStateException("Task is cancelled"); }
  }
  
  /** Check whether the task has produced a value (equivalently, whether the status is {@code FINISHED}). */
  public boolean hasValue() { return _status == Status.FINISHED; }
  
  /**
   * Implementation for starting the task.  Should cause the status to be updated to {@code RUNNING}
   * when the task starts (but need not do so directly).  May assume the current status is {@code PAUSED}.
   */
  protected abstract void doStart();
  
  /**
   * Implementation for cancelling the task.  Should dispose of any unneeded resources and cause the 
   * status to be updated to {@code CANCELLED} (but need not do so directly).  May assume the current 
   * status is <em>not</em> {@code CANCELLED}.
   */
  protected abstract void doCancel();
  
  /**
   * Implementation for producing the result.  If the task is not {@code FINISHED}, should block until it 
   * is (a resulting {@code InterruptedException} need not be handled).  May assume the current status is 
   * <em>not</em> {@code CANCELLED}.
   * @throws InterruptedException  If the current thread is interrupted while waiting for a result
   * @throws InvocationTargetException  If <em>any</em> {@code Throwable} is thrown while executing the task.
   *                                    This follows the precedent of the {@code java.reflect} APIs, which wrap
   *                                    exceptions, errors, and throwables in 
   *                                    {@code java.lang.reflect.InvocationTargetException}s.
   * @throws Exception  If anything goes wrong in the task-running implementation.
   */
  protected abstract R getValue() throws Exception;

  public static enum Status {
    /** The task is not currently running, but can be started. */
    PAUSED,
    /** The task is currently executing. */
    RUNNING,
    /** The task has completed, and the result is available. */
    FINISHED,
    /** The task has been stopped and cannot be restarted.  No result is available. */
    CANCELLED;
  }
  
}