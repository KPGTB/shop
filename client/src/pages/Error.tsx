import {Link, useRouteError} from "react-router-dom"

import styles from "./Error.module.scss"

const Error = () => {
	let error = useRouteError() as {
		status?: number
		statusText?: string
		message?: string
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
