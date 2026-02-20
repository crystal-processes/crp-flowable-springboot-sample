import { useState, useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import './App.css'
import TasksTab from './TasksTab'
import ProcessesTab from './ProcessesTab'
import StatusTab from './StatusTab'
import ProcessInstancesTab from './ProcessInstancesTab'
import { useAuth } from './AuthContext'
import { makeAuthenticatedRequest } from './utils/api'

function App() {
  const { user, logout } = useAuth()
  const navigate = useNavigate()
  const [tasks, setTasks] = useState([])
  const [processes, setProcesses] = useState([])
  const [processInstances, setProcessInstances] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)
  const [selectedTask, setSelectedTask] = useState(null)
  const [autoRefresh, setAutoRefresh] = useState(true)
  const [successMessage, setSuccessMessage] = useState(null)
  const [activeTab, setActiveTab] = useState('tasks')
  const [statusSubTab, setStatusSubTab] = useState('processes')

  // Fetch tasks from Flowable REST API
  // Endpoint: GET /process-api/runtime/tasks
  const fetchTasks = async () => {
    setLoading(true)
    setError(null)
    try {
      const response = await makeAuthenticatedRequest('/process-api/runtime/tasks?sort=createTime&order=desc&start=0&size=50')
      if (response.ok) {
        const json = await response.json()
        // Important: Flowable returns data in 'data' property
        const tasksData = json.data || []
        setTasks(Array.isArray(tasksData) ? tasksData : [])
        console.log('Tasks loaded from Flowable API:', json)
      } else {
        setError('Failed to fetch tasks from Flowable')
        setTasks([])
      }
    } catch (error) {
      console.error('Error fetching tasks:', error)
      setError(`Connection error: ${error.message}`)
      setTasks([])
    } finally {
      setLoading(false)
    }
  }

  // Fetch process definitions from Flowable REST API
  // Endpoint: GET /process-api/repository/process-definitions
  const fetchProcesses = async () => {
    try {
      const response = await makeAuthenticatedRequest('/process-api/repository/process-definitions?latest=true&sort=name&order=asc&start=0&size=50')
      if (response.ok) {
        const json = await response.json()
        // Important: Flowable returns data in 'data' property
        const processesData = json.data || []
        setProcesses(Array.isArray(processesData) ? processesData : [])
        console.log('Processes loaded from Flowable API:', json)
      }
    } catch (error) {
      console.error('Error fetching processes:', error)
    }
  }

  // Fetch process instances from Flowable REST API
  // Endpoint: GET /process-api/runtime/process-instances
  const fetchProcessInstances = async () => {
    setLoading(true)
    setError(null)
    try {
      const response = await makeAuthenticatedRequest('/process-api/runtime/process-instances?sort=startTime&order=desc&start=0&size=50')
      if (response.ok) {
        const json = await response.json()
        const instancesData = json.data || []
        setProcessInstances(Array.isArray(instancesData) ? instancesData : [])
        console.log('Process instances loaded from Flowable API:', json)
      } else {
        setError('Failed to fetch process instances')
        setProcessInstances([])
      }
    } catch (error) {
      console.error('Error fetching process instances:', error)
      setError(`Connection error: ${error.message}`)
      setProcessInstances([])
    } finally {
      setLoading(false)
    }
  }

  // Auto-load tasks on component mount
  useEffect(() => {
    fetchTasks()
    fetchProcessInstances()
  }, [])

  // Dynamic auto-refresh based on active tab
  useEffect(() => {
    if (!autoRefresh) return

    let interval

    if (activeTab === 'tasks') {
      // Refresh tasks every 10 seconds when on Tasks tab
      interval = setInterval(() => {
        fetchTasks()
      }, 10000)
    } else if (activeTab === 'instances') {
      // Refresh process instances every 10 seconds when on Process Instances tab
      interval = setInterval(() => {
        fetchProcessInstances()
      }, 10000)
    } else if (activeTab === 'status') {
      // Refresh tasks and processes every 10 seconds when on Status tab
      interval = setInterval(() => {
        fetchTasks()
        fetchProcesses()
      }, 10000)
    }

    return () => {
      if (interval) clearInterval(interval)
    }
  }, [autoRefresh, activeTab])

  // ...existing code...

  // Navigate to process instances tab
  const navigateToProcessInstance = (instanceId) => {
    setActiveTab('instances')
  }

  // ...existing code...

  return (
    <div className="app-layout">
      {/* Main Content */}
      <main className="main-content">
        {/* Top Navigation Bar */}
        <nav className="navbar">
          <div className="navbar-content">
            <div className="navbar-title">
              <h2>
                  <img src="/favicon.svg" alt="CRP Logo" width="64" height="64"/>
              </h2>
            </div>
            <div className="navbar-tabs">
              <button
                className={`navbar-tab ${activeTab === 'tasks' ? 'active' : ''}`}
                onClick={() => {
                  setActiveTab('tasks')
                  fetchTasks()
                }}
              >
                📋 Active Tasks ({tasks.length})
              </button>
              <button
                className={`navbar-tab ${activeTab === 'instances' ? 'active' : ''}`}
                onClick={() => {
                  setActiveTab('instances')
                  fetchProcessInstances()
                }}
              >
                🔄 Process Instances ({processInstances.length})
              </button>
              <button
                className={`navbar-tab ${activeTab === 'status' ? 'active' : ''}`}
                onClick={() => {
                  setActiveTab('status')
                  fetchProcesses()
                }}
              >
                📊 Status
              </button>
            </div>
            <div className="navbar-status">
              <div className="user-info">
                <span>👤 {user?.username || 'User'}</span>
                <button
                  onClick={() => {
                    logout()
                    navigate('/login')
                  }}
                  className="btn-logout"
                  title="Logout"
                >
                  🚪 Logout
                </button>
              </div>
            </div>
          </div>
        </nav>

        {/* Alerts */}
        {error && (
          <div className="alert alert-error">
            <strong>⚠️ Error:</strong> {error}
            <button onClick={fetchTasks} className="retry-btn">Retry</button>
          </div>
        )}

        {successMessage && (
          <div className="alert alert-success">
            <strong>✅ Success:</strong> {successMessage}
            <button onClick={() => setSuccessMessage(null)} className="close-btn">✕</button>
          </div>
        )}

        {/* Tab Contents */}
        <div className="tabs-content">
          {activeTab === 'tasks' && (
            <TasksTab
              tasks={tasks}
              selectedTask={selectedTask}
              setSelectedTask={setSelectedTask}
              loading={loading}
              fetchTasks={fetchTasks}
              fetchProcessInstances={fetchProcessInstances}
              onNavigateToInstance={navigateToProcessInstance}
            />
          )}

          {activeTab === 'instances' && (
            <ProcessInstancesTab
              loading={loading}
              error={error}
              setError={setError}
              processInstances={processInstances}
              fetchProcessInstances={fetchProcessInstances}
            />
          )}

          {activeTab === 'status' && (
            <StatusTab
              tasks={tasks}
              processes={processes}
              loading={loading}
              autoRefresh={autoRefresh}
              setAutoRefresh={setAutoRefresh}
              fetchTasks={fetchTasks}
              fetchProcessInstances={fetchProcessInstances}
              error={error}
              setError={setError}
              successMessage={successMessage}
              setSuccessMessage={setSuccessMessage}
              statusSubTab={statusSubTab}
              setStatusSubTab={setStatusSubTab}
            />
          )}
        </div>
      </main>
    </div>
  )
}

export default App
