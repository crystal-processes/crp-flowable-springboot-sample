import { useState } from 'react'

function TasksTab({ tasks, selectedTask, setSelectedTask, loading, fetchTasks, onNavigateToInstance }) {
  return (
    <div className="tab-content">

      {loading && tasks.length === 0 ? (
        <p className="loading-message">⏳ Loading tasks from Flowable...</p>
      ) : tasks.length > 0 ? (
        <div className="tasks-list">
          {tasks.map((task) => (
            <div
              key={task.id}
              className={`task-item ${selectedTask?.id === task.id ? 'selected' : ''}`}
              onClick={() => setSelectedTask(task)}
            >
              <div className="task-header">
                <h3>{task.name}</h3>
                <span className="task-id">ID: {task.id}</span>
              </div>
              <div className="task-info">
                <p><strong>Assignee:</strong> {task.assignee || 'Unassigned'}</p>
                <p>
                  <strong>Process:</strong>{' '}
                  <button
                    className="link-button"
                    onClick={(e) => {
                      e.stopPropagation()
                      onNavigateToInstance && onNavigateToInstance(task.processInstanceId)
                    }}
                    title="Go to process instance details"
                  >
                    {task.processDefinitionId} → {task.processInstanceId.substring(0, 8)}...
                  </button>
                </p>
                {task.dueDate && (
                  <p><strong>Due Date:</strong> {new Date(task.dueDate).toLocaleDateString()}</p>
                )}
                {task.createTime && (
                  <p><strong>Created:</strong> {new Date(task.createTime).toLocaleString()}</p>
                )}
              </div>
            </div>
          ))}
        </div>
      ) : (
        <p className="empty-message">✅ No tasks currently assigned. Great job!</p>
      )}

      {selectedTask && (
        <div className="task-details-panel">
          <div className="task-details-header">
            <h3>📝 Task Details</h3>
            <button onClick={() => setSelectedTask(null)} className="close-icon">✕</button>
          </div>
          <div className="task-details">
            <p><strong>Task ID:</strong> <code>{selectedTask.id}</code></p>
            <p><strong>Name:</strong> {selectedTask.name}</p>
            <p><strong>Assignee:</strong> {selectedTask.assignee || 'Unassigned'}</p>
            <p><strong>Process Definition ID:</strong> {selectedTask.processDefinitionId}</p>
            <p><strong>Process Instance ID:</strong> {selectedTask.processInstanceId}</p>
            {selectedTask.description && (
              <p><strong>Description:</strong> {selectedTask.description}</p>
            )}
            {selectedTask.dueDate && (
              <p><strong>Due Date:</strong> {new Date(selectedTask.dueDate).toLocaleString()}</p>
            )}
            {selectedTask.createTime && (
              <p><strong>Created:</strong> {new Date(selectedTask.createTime).toLocaleString()}</p>
            )}
            {selectedTask.priority !== undefined && (
              <p><strong>Priority:</strong> {selectedTask.priority}</p>
            )}
          </div>
        </div>
      )}
    </div>
  )
}

export default TasksTab
