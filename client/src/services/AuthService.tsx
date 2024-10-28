import {redirect} from "react-router-dom"

class AuthService {
	user: User | undefined

	fetchUser = async () => {
		try {
			const res = await fetch(API_URL + "/user", {
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
	if (!userData) {
		return redirect("/error?status=403&message=Forbidden")
	}
	return true
}

const unauthenticatedMiddleware = (authService: AuthService) => {
	const userData = authService.getUser()
	if (userData) {
		return redirect("/error?status=403&message=Forbidden")
	}
	return true
}

const roleMiddleware = (authService: AuthService, role: UserRole) => {
	const userData = authService.getUser()
	if (!userData || userData.role != role) {
		return redirect("/error?status=403&message=Forbidden")
	}
	return true
}

export default AuthService
export {unauthenticatedMiddleware, authenticatedMiddleware, roleMiddleware}
