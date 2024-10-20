import {redirect} from "react-router-dom"

class AuthService {
	user: User | undefined

	fetchUser = async () => {
		try {
			const res = await fetch(API_URL + "/auth/info", {
				credentials: "include",
			})

			if (res.status !== 200) {
				this.user = undefined
				return
			}

			const json = await res.json()
			this.user = json.data
		} catch (error) {
			this.user = undefined
		}
	}

	getUser = () => this.user
	isLogged = () => this.user !== undefined
}

const authenticatedMiddleware = (authService: AuthService) => {
	const userData = authService.getUser()
	console.log(userData)
	if (!userData) {
		return redirect("/error?status=403&message=Forbidden")
	}
	return true
}

const unauthenticatedMiddleware = (authService: AuthService) => {
	const userData = authService.getUser()
	console.log(userData)
	if (userData) {
		return redirect("/error?status=403&message=Forbidden")
	}
	return true
}

const roleMiddleware = (authService: AuthService, role: UserRole) => {
	const userData = authService.getUser()
	console.log(userData)
	if (!userData || userData.role != role) {
		return redirect("/error?status=403&message=Forbidden")
	}
	return true
}

type User = {
	email: string
	role: UserRole
	name: string
	surname: string
	birthDate: Date
}

type UserRole = "CUSTOMER" | "BUSINESS"

export default AuthService
export {unauthenticatedMiddleware, authenticatedMiddleware, roleMiddleware}
export type {User, UserRole}
