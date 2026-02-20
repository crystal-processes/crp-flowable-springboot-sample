import { useState } from 'react'
import FormEngine from './FormEngine'

function TasksTab({ tasks, selectedTask, setSelectedTask, loading, fetchTasks, onNavigateToInstance, fetchProcessInstances }) {
  const [showForm, setShowForm] = useState(false)
  const [formSubmitMessage, setFormSubmitMessage] = useState(null)

  const handleFormSubmit = (formData, result) => {
    setFormSubmitMessage('✅ Task form submitted successfully!')
    setShowForm(false)
    setSelectedTask(null)

    // Refresh tasks and process instances to reflect any changes
    fetchTasks()
    if (fetchProcessInstances) {
      fetchProcessInstances()
    }

    // Clear success message after 3 seconds
    setTimeout(() => setFormSubmitMessage(null), 3000)
  }

  const handleFormClose = () => {
    setShowForm(false)
  }
  return (
    <div className="tab-content">
      {formSubmitMessage && (
        <div className="alert alert-success">
          <strong>{formSubmitMessage}</strong>
          <button onClick={() => setFormSubmitMessage(null)} className="close-btn">✕</button>
        </div>
      )}

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

      {selectedTask && !showForm && (
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
            <button
              onClick={() => setShowForm(true)}
              className="btn-form-submit"
              title="Fill and submit the task form"
            >
              📋 Fill Task Form
            </button>
          </div>
        </div>
      )}

      {selectedTask && showForm && (
        <FormEngine
          taskId={selectedTask.id}
          onSubmit={handleFormSubmit}
          onClose={handleFormClose}
        />
      )}
    </div>
  )
}

export default TasksTab
