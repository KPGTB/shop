import {useAuth} from "../../../context/AuthContext"

const BusinessDashboardPage = () => {
	const [logged, user] = useAuth()

	return (
		<>
			<h1>
				Hello {user?.name} {user?.surname} in business dashboard
			</h1>
			<form
				method="POST"
				action={`${API_URL}/auth/signout`}
			>
				<button>Logout</button>
			</form>
		</>
	)
}

export default BusinessDashboardPage
