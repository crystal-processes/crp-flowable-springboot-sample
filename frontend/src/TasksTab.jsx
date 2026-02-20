import { useState } from 'react'
import FormEngine from './FormEngine'

function TasksTab({ tasks, selectedTask, setSelectedTask, loading, fetchTasks, onNavigateToInstance, fetchProcessInstances, taskFilter, setTaskFilter, taskLimit, taskFilterType, setTaskFilterType }) {
  const [formSubmitMessage, setFormSubmitMessage] = useState(null)

  const handleFilterChange = (e) => {
    const newFilter = e.target.value
    setTaskFilter(newFilter)
    // Fetch tasks with new filter and current filter type
    fetchTasks(newFilter, taskFilterType)
  }

  const handleFilterTypeChange = (e) => {
    const newType = e.target.value
    setTaskFilterType(newType)
    // Fetch tasks with new filter type and current filter
    fetchTasks(taskFilter, newType)
  }

  const handleFormSubmit = (formData, result) => {
    setFormSubmitMessage('✅ Task form submitted successfully!')
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
    setSelectedTask(null)
  }
  return (
    <div className="tab-content">
      {formSubmitMessage && (
        <div className="alert alert-success">
          <strong>{formSubmitMessage}</strong>
          <button onClick={() => setFormSubmitMessage(null)} className="close-btn">✕</button>
        </div>
      )}

      {/* Task Filter Input */}
      <div className="task-filter-section">
        <select
          value={taskFilterType}
          onChange={handleFilterTypeChange}
          className="task-filter-type-select"
        >
          <option value="name">🏷️ Task Name</option>
          <option value="businessKey">🔑 Business Key</option>
          <option value="processName">⚙️ Process Name</option>
          <option value="taskKey">📌 Task Definition Key</option>
        </select>
        <input
          type="text"
          placeholder="🔍 Enter search term..."
          value={taskFilter}
          onChange={handleFilterChange}
          className="task-filter-input"
        />
        {taskFilter && (
          <button
            onClick={() => {
              setTaskFilter('')
              fetchTasks('', taskFilterType)
            }}
            className="btn-clear-filter"
          >
            ✕ Clear Filter
          </button>
        )}
        <span className="task-count">Showing {tasks.length} tasks (Limit: {taskLimit})</span>
      </div>

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
                {(selectedTask) && (selectedTask.id === task.id) && (
                  <button
                    onClick={(e) => {
                      e.stopPropagation()
                      setSelectedTask(null)
                    }}
                    className="toggle-details-btn toggle-expanded"
                    title="Hide task details"
                  >
                    ▲
                  </button>
                )}
                {(!selectedTask || (selectedTask.id !== task.id)) && (
                  <span className="toggle-details-icon" title="Show task details">
                    ▼
                  </span>
                )}
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
                    {task.processDefinitionId} → {(task.processInstanceId) ? task.processInstanceId.substring(0, 8) : 'no processInstanceId'}...
                  </button>
                </p>
                {task.dueDate && (
                  <p><strong>Due Date:</strong> {new Date(task.dueDate).toLocaleDateString()}</p>
                )}
                {task.createTime && (
                  <p><strong>Created:</strong> {new Date(task.createTime).toLocaleString()}</p>
                )}
              </div>
              {(selectedTask) && (selectedTask.id === task.id) && (
                  <div className="task-details-panel">
                  {(task.formKey) ?
                            <FormEngine
                                taskId={selectedTask.id}
                                onSubmit={handleFormSubmit}
                                onClose={handleFormClose}
                            />
                  : 'Without form'}
                  </div>
              )}
            </div>
          ))}
        </div>
      ) : (
        <p className="empty-message">✅ No tasks currently assigned. Great job!</p>
      )}
    </div>
  )
}

export default TasksTab
