import "./globals.scss"

import ReactDOM from "react-dom/client"
import {createBrowserRouter, RouterProvider} from "react-router-dom"

import Layout from "./pages/layout"

const router = createBrowserRouter([
	{
		path: "/",
		element: <Layout />,
	},
])

ReactDOM.createRoot(document.getElementById("root")!).render(
	<RouterProvider router={router} />
)
