import "./globals.scss"

import ReactDOM from "react-dom/client"
import {createBrowserRouter, Outlet, RouterProvider} from "react-router-dom"

import BusinessDashboardPage from "./pages/[business]/Dashboard/DashboardPage"
import BusinessLayout from "./pages/[business]/Layout"
import businessProductsLoader from "./pages/[business]/Products/ProductsLoader"
import BusinessProductsPage from "./pages/[business]/Products/ProductsPage"
import CustomerDashboardPage from "./pages/[customer]/Dashboard/DashboardPage"
import Error from "./pages/Error"
import LandingPage from "./pages/Landing/LandingPage"
import Layout from "./pages/Layout"
import SigninPage from "./pages/Signin/SigninPage"
import AuthService, {
	roleMiddleware,
	unauthenticatedMiddleware,
} from "./services/AuthService"

const authService = new AuthService()
await authService.fetchUser()

const router = createBrowserRouter([
	{
		path: "/",
		element: <Layout />,
		errorElement: <Error />,
		loader: () => {
			return [authService.getUser(), authService]
		},
		children: [
			{index: true, element: <LandingPage />},
			{
				path: "signin",
				loader: () => unauthenticatedMiddleware(authService),
				element: <SigninPage />,
			},
			{
				path: "customer",
				element: <Outlet />,
				loader: () => roleMiddleware(authService, "CUSTOMER"),
				children: [
					{path: "dashboard", element: <CustomerDashboardPage />},
				],
			},
			{
				path: "business",
				element: <BusinessLayout />,
				loader: () => roleMiddleware(authService, "BUSINESS"),
				children: [
					{path: "dashboard", element: <BusinessDashboardPage />},
					{
						path: "products",
						element: <BusinessProductsPage />,
						loader: businessProductsLoader,
					},
				],
			},
		],
	},
	{
		path: "/error",
		element: <Error />,
		errorElement: <Error />,
	},
])

ReactDOM.createRoot(document.getElementById("root")!).render(
	<RouterProvider router={router} />
)
