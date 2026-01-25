import { useState } from 'react'

function ProcessInstancesTab({ loading, error, setError, processInstances, fetchProcessInstances }) {
  const [selectedInstance, setSelectedInstance] = useState(null)

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
        </div>
      )}
    </div>
  )
}

export default ProcessInstancesTab
