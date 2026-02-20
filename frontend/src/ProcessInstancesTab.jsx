import { useState, useEffect } from 'react'
import { makeAuthenticatedRequest } from './utils/api'

function ProcessInstancesTab({ loading, error, setError, processInstances, fetchProcessInstances }) {
  const [selectedInstance, setSelectedInstance] = useState(null)
  const [diagramLoading, setDiagramLoading] = useState(false)
  const [diagramImage, setDiagramImage] = useState(null)
  const [diagramError, setDiagramError] = useState(null)

  // Fetch process diagram when instance is selected
  useEffect(() => {
    if (selectedInstance) {
      fetchProcessDiagram(selectedInstance.id)
    } else {
      setDiagramImage(null)
      setDiagramError(null)
    }
  }, [selectedInstance])

  const fetchProcessDiagram = async (processInstanceId) => {
    setDiagramLoading(true)
    setDiagramError(null)
    try {
      const response = await makeAuthenticatedRequest(
        `/process-api/runtime/process-instances/${processInstanceId}/diagram`,
        {
          method: 'GET'
        }
      )

      if (response.ok) {
        // Get the blob and create an object URL for the image
        const blob = await response.blob()
        const imageUrl = URL.createObjectURL(blob)
        setDiagramImage(imageUrl)
        console.log('Process diagram loaded:', imageUrl)
      } else if (response.status === 404) {
        setDiagramError('Process diagram not available for this instance')
        setDiagramImage(null)
      } else {
        setDiagramError('Failed to load process diagram')
        setDiagramImage(null)
      }
    } catch (err) {
      console.error('Error fetching process diagram:', err)
      setDiagramError(`Error loading diagram: ${err.message}`)
      setDiagramImage(null)
    } finally {
      setDiagramLoading(false)
    }
  }

  return (
    <div className="tab-content">
      {loading && processInstances.length === 0 ? (
        <p className="loading-message">⏳ Loading process instances from Flowable...</p>
      ) : processInstances.length > 0 ? (
        <div className="processes-list">
          {processInstances.map((instance) => (
            <div
              key={instance.id}
              className={`process-item ${selectedInstance?.id === instance.id ? 'selected' : ''}`}
              onClick={() => setSelectedInstance(instance)}
            >
              <h4>{instance.processDefinitionKey}</h4>
              <p className="process-id">Instance ID: {instance.id}</p>
              <p className="process-name">Instance Name: {instance.name}</p>
              <p className="process-business-key">Business Key: {instance.businessKey || 'N/A'}</p>
              {instance.startTime && (
                <p className="process-start-time">Started: {new Date(instance.startTime).toLocaleString()}</p>
              )}
              <p className="process-status">Status: {instance.suspended ? 'Suspended' : 'Running'}</p>
            </div>
          ))}
        </div>
      ) : (
        <p className="empty-message">ℹ️ No process instances available</p>
      )}

      {selectedInstance && (
        <div className="instance-details-panel">
          <div className="instance-details-header">
            <h3>📋 Process Instance Details</h3>
            <button onClick={() => setSelectedInstance(null)} className="close-icon">✕</button>
          </div>
          <div className="instance-details">
            <p><strong>Instance ID:</strong> <code>{selectedInstance.id}</code></p>
            <p><strong>Name:</strong> {selectedInstance.name}</p>
            {selectedInstance.processDefinitionId && (
              <p><strong>Process Definition ID:</strong> {selectedInstance.processDefinitionId}</p>
            )}
            {selectedInstance.businessKey && (
              <p><strong>Business Key:</strong> {selectedInstance.businessKey}</p>
            )}
            {selectedInstance.startTime && (
              <p><strong>Start Time:</strong> {new Date(selectedInstance.startTime).toLocaleString()}</p>
            )}
            {selectedInstance.endTime && (
              <p><strong>End Time:</strong> {new Date(selectedInstance.endTime).toLocaleString()}</p>
            )}
            <p><strong>Status:</strong> {selectedInstance.suspended ? 'Suspended' : 'Running'}</p>
            {selectedInstance.tenantId && (
              <p><strong>Tenant ID:</strong> {selectedInstance.tenantId}</p>
            )}
          </div>

          {/* Process Diagram Section */}
          <div className="diagram-section">
            <h4>📊 Process Diagram</h4>
            {diagramLoading ? (
              <div className="diagram-loading">
                <p>⏳ Loading process diagram...</p>
              </div>
            ) : diagramError ? (
              <div className="diagram-error">
                <p>⚠️ {diagramError}</p>
              </div>
            ) : diagramImage ? (
              <div className="diagram-container">
                <img
                  src={diagramImage}
                  alt="Process Diagram"
                  className="process-diagram"
                />
              </div>
            ) : (
              <div className="diagram-empty">
                <p>No diagram available</p>
              </div>
            )}
          </div>
        </div>
      )}
    </div>
  )
}

export default ProcessInstancesTab
