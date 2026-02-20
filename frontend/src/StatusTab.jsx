import ProcessesTab from './ProcessesTab'

function StatusTab({
  tasks,
  processes,
  loading,
  autoRefresh,
  setAutoRefresh,
  fetchTasks,
  fetchProcessInstances,
  error,
  setError,
  successMessage,
  setSuccessMessage,
  statusSubTab,
  setStatusSubTab,
  taskLimit,
  setTaskLimit
}) {
  return (
    <div className="tab-content">
      {/* Sub-tabs navigation */}
      <div className="subtabs-nav">
        <button
          className={`subtab-button ${statusSubTab === 'processes' ? 'active' : ''}`}
          onClick={() => setStatusSubTab('processes')}
        >
          ⚙️ Process Definitions
        </button>
        <button
          className={`subtab-button ${statusSubTab === 'system' ? 'active' : ''}`}
          onClick={() => setStatusSubTab('system')}
        >
          🖥️ System
        </button>
      </div>

      {/* System Sub-tab */}
      {statusSubTab === 'system' && (
        <div className="subtab-content">
          <div className="status-overview">
            <div className="status-grid">
          <div className="status-card">
            <div className="status-icon">📋</div>
            <div className="status-info">
              <p className="status-title">Active Tasks</p>
              <p className="status-value">{tasks.length}</p>
            </div>
          </div>

          <div className="status-card">
            <div className="status-icon">⚙️</div>
            <div className="status-info">
              <p className="status-title">Process Definitions</p>
              <p className="status-value">{processes.length}</p>
            </div>
          </div>

          <div className="status-card">
            <div className="status-icon">{loading ? '⏳' : '✅'}</div>
            <div className="status-info">
              <p className="status-title">System Status</p>
              <p className="status-value">{loading ? 'Loading...' : 'Ready'}</p>
            </div>
          </div>

          <div className="status-card">
            <div className="status-icon">🔄</div>
            <div className="status-info">
              <p className="status-title">Auto-Refresh</p>
              <p className="status-value">{autoRefresh ? 'Enabled' : 'Disabled'}</p>
            </div>
          </div>
        </div>

        <div className="status-actions">
          <button onClick={fetchTasks} disabled={loading} className="btn-primary">
            {loading ? '⏳ Refreshing...' : '🔄 Refresh Now'}
          </button>

          <label className="checkbox-label">
            <input
              type="checkbox"
              checked={autoRefresh}
              onChange={(e) => setAutoRefresh(e.target.checked)}
            />
            Enable auto-refresh every 30 seconds
          </label>

          <label className="task-limit-label">
            <span>📊 Task Limit:</span>
            <input
              type="number"
              min="1"
              max="500"
              value={taskLimit}
              onChange={(e) => {
                const newLimit = parseInt(e.target.value, 10)
                if (newLimit > 0) {
                  setTaskLimit(newLimit)
                }
              }}
              className="task-limit-input"
            />
          </label>
        </div>

        <div className="status-details">
          <h3>📈 Details</h3>
          <div className="details-grid">
            <div className="detail-item">
              <span className="detail-label">Total Tasks:</span>
              <span className="detail-value">{tasks.length}</span>
            </div>
            <div className="detail-item">
              <span className="detail-label">Total Processes:</span>
              <span className="detail-value">{processes.length}</span>
            </div>
            <div className="detail-item">
              <span className="detail-label">Last Refresh:</span>
              <span className="detail-value">{new Date().toLocaleTimeString()}</span>
            </div>
            <div className="detail-item">
              <span className="detail-label">System Health:</span>
              <span className="detail-value" style={{ color: '#4CAF50' }}>✅ Healthy</span>
            </div>
          </div>
        </div>

        <div className="info-card">
          <h3>💡 Information</h3>
          <ul>
            <li>Tasks are automatically loaded when the app starts</li>
            <li>Use the "Tasks" tab to view and manage your tasks</li>
            <li>Use the "Process Definitions" sub-tab to start new process instances</li>
            <li>Enable auto-refresh to keep data synchronized</li>
            <li>All data comes directly from the Flowable REST API</li>
          </ul>
        </div>
      </div>
        </div>
      )}

      {/* Process Definitions Sub-tab */}
      {statusSubTab === 'processes' && (
        <div className="subtab-content">
          <ProcessesTab
            processes={processes}
            loading={loading}
            error={error}
            setError={setError}
            successMessage={successMessage}
            setSuccessMessage={setSuccessMessage}
            fetchProcessInstances={fetchProcessInstances}
            fetchTasks={fetchTasks}
          />
        </div>
      )}
    </div>
  )
}

export default StatusTab
