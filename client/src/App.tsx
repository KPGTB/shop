import "./globals.scss"

import ReactDOM from "react-dom/client"
import {createBrowserRouter, RouterProvider} from "react-router-dom"

import AccountPageLayout from "./components/layout/AccountPageLayout/AccountPageLayout"
import {
	allOrdersLoader,
	ownOrdersLoader,
} from "./pages/[account]/Orders/OrdersLoader"
import OrdersPage from "./pages/[account]/Orders/OrdersPage"
import BusinessDashboardPage from "./pages/[business]/Dashboard/DashboardPage"
import businessProductsLoader from "./pages/[business]/Products/ProductsLoader"
import BusinessProductsPage from "./pages/[business]/Products/ProductsPage"
import CustomerDashboardPage from "./pages/[customer]/Dashboard/DashboardPage"
import cartLoader from "./pages/Cart/CartLoader"
import CartPage from "./pages/Cart/CartPage"
import categoryLoader from "./pages/Category/CategoryLoader"
import CategoryPage from "./pages/Category/CategoryPage"
import productLoader from "./pages/Category/Product/ProductLoader"
import ProductPage from "./pages/Category/Product/ProductPage"
import Error from "./pages/Error"
import LandingPage from "./pages/Landing/LandingPage"
import Layout from "./pages/Layout"
import PaymentPage from "./pages/Payment/PaymentPage"
import SigninPage from "./pages/Signin/SigninPage"
import SignupPage from "./pages/Signup/SignupPage"
import VerificationPage from "./pages/Verification/VerificationPage"
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
				path: "signup",
				loader: () => unauthenticatedMiddleware(authService),
				element: <SignupPage />,
			},
			{
				path: "signup/activate",
				loader: () => unauthenticatedMiddleware(authService),
				element: (
					<VerificationPage
						title="Activate account"
						endpoint="/auth/activate"
						method="POST"
						buttonText="Activate"
						successRedirect="/signin?activated"
					/>
				),
			},
			{
				path: "newsletter/verify",
				element: (
					<VerificationPage
						title="Subscribe newsletter"
						endpoint="/newsletter/verify"
						method="POST"
						buttonText="Subscribe"
						successRedirect="/"
					/>
				),
			},
			{
				path: "newsletter/unsubscribe",
				element: (
					<VerificationPage
						title="Unsubscribe newsletter"
						endpoint="/newsletter"
						method="DELETE"
						buttonText="Unsubscribe"
						successRedirect="/"
					/>
				),
			},
			{
				path: "customer",
				element: (
					<AccountPageLayout
						links={[
							{to: "/customer/dashboard", label: "Dashboard"},
							{to: "/customer/orders", label: "Orders"},
							{to: "/customer/settings", label: "Settings"},
						]}
					/>
				),
				loader: () => roleMiddleware(authService, "CUSTOMER"),
				children: [
					{path: "dashboard", element: <CustomerDashboardPage />},
					{
						path: "orders",
						element: <OrdersPage />,
						loader: ownOrdersLoader,
					},
				],
			},
			{
				path: "business",
				element: (
					<AccountPageLayout
						links={[
							{to: "/business/dashboard", label: "Dashboard"},
							{to: "/business/products", label: "Products"},
							{to: "/business/discounts", label: "Discounts"},
							{to: "/business/orders", label: "Orders"},
							{to: "/business/newsletter", label: "Newsletter"},
							{to: "/business/apparance", label: "Apparance"},
							{to: "/business/settings", label: "Settings"},
						]}
					/>
				),
				loader: () => roleMiddleware(authService, "BUSINESS"),
				children: [
					{path: "dashboard", element: <BusinessDashboardPage />},
					{
						path: "products",
						element: <BusinessProductsPage />,
						loader: businessProductsLoader,
					},
					{
						path: "orders",
						element: <OrdersPage />,
						loader: allOrdersLoader,
					},
				],
			},
			{
				path: "shop/:category",
				element: <CategoryPage />,
				loader: categoryLoader,
			},
			{
				path: "shop/:category/:product",
				element: <ProductPage />,
				loader: productLoader,
			},
			{
				path: "cart",
				element: <CartPage />,
				loader: cartLoader,
			},
			{
				path: "payment",
				element: <PaymentPage />,
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
