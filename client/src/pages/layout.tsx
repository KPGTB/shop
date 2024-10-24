import "react-toastify/dist/ReactToastify.css"

import {useCallback, useEffect, useState} from "react"
import {Outlet, useLoaderData} from "react-router-dom"
import {ToastContainer} from "react-toastify"

import AccessibilityProvider from "../components/layout/Accessibility/AccessibilityProvider"
import Cookies from "../components/layout/Cookies/Cookies"
import Footer from "../components/layout/Footer/Footer"
import Header from "../components/layout/Header/Header"
import {AuthProvider} from "../context/AuthContext"
import ThemeContext, {Theme} from "../context/ThemeContext"

const Layout = () => {
	const [user, authService]: AuthLoader = useLoaderData() as AuthLoader
	const [theme, setThemeState] = useState<Theme>("light")

	const setTheme = useCallback((newTheme: Theme) => {
		setThemeState(newTheme)
		localStorage.setItem("theme", newTheme)
		document.body.setAttribute("data-theme", newTheme)
	}, [])

	useEffect(() => {
		let storageTheme = localStorage.getItem("theme")
		setTheme(storageTheme === "dark" ? "dark" : "light")
	}, [])

	return (
		<>
			<ThemeContext.Provider value={theme}>
				<AuthProvider
					user={user}
					authService={authService}
				>
					<Header setTheme={setTheme} />
					<main>
						<Outlet />
					</main>
					<Footer />

					<Cookies />
					<AccessibilityProvider />
					<ToastContainer />
				</AuthProvider>
			</ThemeContext.Provider>
		</>
	)
}

export default Layout
export type {Theme}
