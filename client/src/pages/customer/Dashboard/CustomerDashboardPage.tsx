import {useEffect, useState} from "react"

const ClientDashboardPage = () => {
	const [data, setData] = useState<string>("nothing")

	useEffect(() => {
		fetch(`${API_URL}/ping`, {
			method: "POST",
			credentials: "include",
		})
			.then((res) => res.json())
			.then((json) => {
				setData(json.date + " " + json.data)
			})
	}, [])

	return (
		<>
			<h1>{data}</h1>
			<form
				method="POST"
				action={`${API_URL}/auth/signout`}
			>
				<button>Logout</button>
			</form>
		</>
	)
}

export default ClientDashboardPage
