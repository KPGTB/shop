import "./globals.scss"

import ReactDOM from "react-dom/client"
import {createBrowserRouter, RouterProvider} from "react-router-dom"

import Error from "./pages/Error"
import LandingPage from "./pages/Landing/LandingPage"
import Layout from "./pages/Layout"

const router = createBrowserRouter([
	{
		path: "/",
		element: <Layout />,
		errorElement: <Error />,
		children: [{index: true, element: <LandingPage />}],
	},
])

ReactDOM.createRoot(document.getElementById("root")!).render(
	<RouterProvider router={router} />
)
