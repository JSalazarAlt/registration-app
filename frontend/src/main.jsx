import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import './index.css'
import App from './App.jsx'

/**
 * Main entry point for the Expense Tracker React application.
 * 
 * This file initializes the React application by mounting the root App component
 * to the DOM. StrictMode is enabled to help identify potential problems in the
 * application during development.
 * 
 * @author Joel Salazar
 * @since 1.0
 */
createRoot(document.getElementById('root')).render(
  <StrictMode>
    <App />
  </StrictMode>,
)
