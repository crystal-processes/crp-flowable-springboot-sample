# CRP Flowable Frontend

A modern React frontend for the CRP Flowable Spring Boot application.

## Getting Started

### Prerequisites
- Node.js 16+ and npm

### Installation

1. Install dependencies:
```bash
cd frontend
npm install
```

2. Create a `.env` file from `.env.example`:
```bash
cp .env.example .env
```

3. Start the development server:
```bash
npm run dev
```

The frontend will be available at `http://localhost:5173` with API proxy to `http://localhost:8080`.

### Build for Production

```bash
npm run build
```

The build artifacts will be stored in the `dist/` directory.

### Preview Production Build

```bash
npm run preview
```

## Project Structure

```
frontend/
├── src/
│   ├── App.jsx           # Main App component
│   ├── App.css           # App component styles
│   ├── main.jsx          # React entry point
│   └── index.css         # Global styles
├── public/               # Static assets
├── index.html            # HTML entry point
├── package.json          # Dependencies and scripts
├── vite.config.js        # Vite configuration
├── .eslintrc.cjs         # ESLint configuration
├── .env.example          # Environment variables template
└── .gitignore
```

## Development

The Vite development server is configured with a proxy to forward API calls to the Spring Boot backend running on `localhost:8080`.

## Scripts

- `npm run dev` - Start development server
- `npm run build` - Build for production
- `npm run preview` - Preview production build
- `npm run lint` - Run ESLint
