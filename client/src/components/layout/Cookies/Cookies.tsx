import {useEffect, useState} from "react"
import {useCookies} from "react-cookie"

import styles from "./Cookies.module.scss"

const Cookies = () => {
	const [cookies, setCookies] = useCookies(["allowCookies"])
	const [show, setShow] = useState<boolean>(false)

	useEffect(() => {
		if (!cookies.allowCookies) {
			setShow(true)
		}
	}, [])

	return (
		<aside className={styles.cookies + (show ? ` ${styles.show}` : "")}>
			We only use cookies that are strictly necessary to ensure the
			website functions properly. If you continue use of this website, we
			assume that you are okay with it.
			<section>
				<button
					onClick={() => {
						setShow(false)
						setCookies("allowCookies", true, {
							path: "/",
							maxAge: 30 * 24 * 60 * 60,
						})
					}}
				>
					Accept
				</button>
			</section>
		</aside>
	)
}

export default Cookies
