import {useContext, useEffect, useState} from "react"
import {MdEmail} from "react-icons/md"
import {RiLockPasswordFill} from "react-icons/ri"
import {Link, useSearchParams} from "react-router-dom"

import ThemeContext from "../../context/ThemeContext"
import {errorIf, successIf} from "../../util/ToastUtl"
import styles from "./SigninPage.module.scss"

const SigninPage = () => {
	const [accType, setAccType] = useState<string>("customer")
	const [params] = useSearchParams()
	const theme = useContext<Theme>(ThemeContext)

	useEffect(() => {
		errorIf(params.has("error"), "Incorrect e-mail or password!", theme)
		successIf(params.has("activated"), "Account activated!", theme)
	}, [])

	return (
		<section className={styles.main}>
			<article className={styles.container}>
				<form
					className={styles.form}
					method="POST"
					action="https://localhost:8080/auth/signin"
				>
					<h1>Sign In</h1>

					<aside className={styles.type}>
						<input
							name="type"
							type="radio"
							id="customer"
							value="customer"
							checked={accType === "customer"}
							onChange={(el) => setAccType(el.target.value)}
						/>{" "}
						<label htmlFor="customer">Customer</label>
						<input
							name="type"
							type="radio"
							id="business"
							value="business"
							checked={accType === "business"}
							onChange={(el) => setAccType(el.target.value)}
						/>{" "}
						<label htmlFor="business">Business</label>
					</aside>

					{accType === "customer" ? (
						<>
							<label htmlFor="email">
								<MdEmail /> E-Mail
							</label>
							<input
								name="email"
								type="email"
								id="email"
							/>
						</>
					) : (
						<input
							name="email"
							type="hidden"
							value={BUSINESS_ACCOUNT_EMAIL}
						/>
					)}

					<label htmlFor="password">
						<RiLockPasswordFill />
						Password
					</label>
					<input
						name="password"
						type="password"
						id="password"
						required
					/>
					<aside>
						<input
							name="remember-me"
							type="checkbox"
							id="remember"
						/>{" "}
						<label htmlFor="remember">Remember me</label>
					</aside>
					<button>Sign In</button>

					<p>
						<Link to="/signin/forgot">Forgot password</Link> •{" "}
						<Link to="/signup">Create account</Link>
					</p>
				</form>
				<aside className={styles.extra}></aside>
			</article>
		</section>
	)
}

export default SigninPage
