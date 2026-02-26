function DynamicComponent({ onDataChange, formData, taskId }) {
  const [localData, setLocalData] = React.useState(formData || {})

  const handleChange = (e) => {
    const { name, value } = e.target
    const newData = { ...localData, [name]: value }
    setLocalData(newData)
    if (onDataChange) {
      onDataChange(newData)
    }
  }

  return React.createElement(
    'div',
    { className: 'simple-form-component' },
    React.createElement('h2', null, 'Hello World'),
    React.createElement('p', null, `This is a simple form component for task: ${taskId}`),
    React.createElement(
      'div',
      { className: 'form-group' },
      React.createElement('label', { htmlFor: 'initiator' }, 'Initiator:'),
      React.createElement('input', {
        type: 'text',
        id: 'initiator',
        name: 'initiator',
        value: localData.initiator || '',
        onChange: handleChange,
        placeholder: 'Enter initiator name'
      })
    ),
    React.createElement(
      'div',
      { className: 'form-group' },
      React.createElement('label', { htmlFor: 'message' }, 'Message:'),
      React.createElement('textarea', {
        id: 'message',
        name: 'message',
        value: localData.message || '',
        onChange: handleChange,
        placeholder: 'Enter your message',
        rows: '4'
      })
    ),
    React.createElement(
      'div',
      { className: 'form-info' },
      React.createElement('p', null, React.createElement('strong', null, 'Current Form Data:')),
      React.createElement('pre', null, JSON.stringify(localData, null, 2))
    )
  )
}

export default DynamicComponent
