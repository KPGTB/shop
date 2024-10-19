import {Link, useRouteError, useSearchParams} from "react-router-dom"

import styles from "./Error.module.scss"

const Error = () => {
	let error = useRouteError() as {
		status?: number
		statusText?: string
		message?: string
	}

	const [params] = useSearchParams()

	if (params.has("status") && params.has("message")) {
		error = {
			status: Number(params.get("status")),
			statusText: params.get("message")!,
		}
	}

	return (
		<main className={styles.error}>
			<h1>Oops!</h1>
			<p>Something went wrong...</p>
			<p className={styles.status}>
				{error.status} - {error.statusText || error.message}
				<br />
				<Link to="/">Back to home page</Link>
			</p>
		</main>
	)
}

export default Error
