import {createContext, PropsWithChildren, useContext, useState} from "react"

import AuthService from "../services/AuthService"

const AuthContext = createContext<Auth>([
	false,
	undefined,
	undefined,
	(newUser: UserDto | undefined, newService: AuthService) => {},
])

const AuthProvider = ({
	authService,
	user,
	children,
}: {
	authService: AuthService
	user: UserDto | undefined
} & PropsWithChildren) => {
	const [userState, setUser] = useState<UserDto | undefined>(user)
	const [serviceState, setService] = useState<AuthService>(authService)

	const update = (newUser: UserDto | undefined, newService: AuthService) => {
		setUser(newUser)
		setService(newService)
	}

	return (
		<AuthContext.Provider
			value={[serviceState.isLogged(), userState, serviceState, update]}
		>
			{children}
		</AuthContext.Provider>
	)
}

const useAuth = () => {
	return useContext<Auth>(AuthContext)
}

export {useAuth, AuthProvider}
