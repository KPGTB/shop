import {useEffect, useState} from "react"

const ClientDashboardPage = () => {
	const [data, setData] = useState<string>("nothing")

	useEffect(() => {
		fetch("https://localhost:8080/ping2", {
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
				action="https://localhost:8080/logout"
			>
				<button>Logout</button>
			</form>
		</>
	)
}

export default ClientDashboardPage
