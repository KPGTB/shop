import "./globals.scss"

import ReactDOM from "react-dom/client"
import {createBrowserRouter, RouterProvider} from "react-router-dom"

import CustomerDashboardPage from "./pages/customer/Dashboard/CustomerDashboardPage"
import Error from "./pages/Error"
import LandingPage from "./pages/Landing/LandingPage"
import Layout from "./pages/Layout"
import SigninPage from "./pages/Signin/SigninPage"

const router = createBrowserRouter([
	{
		path: "/",
		element: <Layout />,
		errorElement: <Error />,
		children: [
			{index: true, element: <LandingPage />},
			{
				path: "/signin",
				element: <SigninPage />,
			},
			{
				path: "/customer",
				element: <CustomerDashboardPage />,
			},
		],
	},
])

ReactDOM.createRoot(document.getElementById("root")!).render(
	<RouterProvider router={router} />
)
