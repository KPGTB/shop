import {useContext, useEffect, useState} from "react"
import ReCAPTCHA from "react-google-recaptcha"
import {MdEmail} from "react-icons/md"
import {RiLockPasswordFill} from "react-icons/ri"
import {Link, useSearchParams} from "react-router-dom"

import ThemeContext from "../../context/ThemeContext"
import {errorIf, successIf} from "../../util/ToastUtl"
import styles from "./SignupPage.module.scss"

const SignupPage = () => {
	const [params] = useSearchParams()
	const theme = useContext<Theme>(ThemeContext)

	const [password, setPassword] = useState<string>("")
	const [captcha, setCaptcha] = useState<string>("")

	useEffect(() => {
		errorIf(params.has("error"), "Something went wrong!", theme)
		errorIf(params.has("exists"), "Account already exists!", theme)
		errorIf(params.has("captcha"), "Captcha failed!", theme)
		successIf(
			params.has("created"),
			"Account created! Please check your e-mail",
			theme
		)
	}, [])

	return (
		<section className={styles.main}>
			<article className={styles.container}>
				<aside className={styles.extra}></aside>
				<form
					className={styles.form}
					method="POST"
					action="https://localhost:8080/auth/signup"
				>
					<h1>Sign Up</h1>

					<label htmlFor="email">
						<MdEmail /> E-Mail
					</label>
					<input
						name="email"
						type="email"
						id="email"
						required
					/>

					<label htmlFor="password">
						<RiLockPasswordFill />
						Password
					</label>
					<input
						name="password"
						type="password"
						id="password"
						onChange={(el) => setPassword(el.target.value)}
						minLength={8}
						maxLength={30}
						required
					/>

					<label htmlFor="password2">
						<RiLockPasswordFill />
						Repeat password
					</label>
					<input
						name="password2"
						type="password"
						id="password2"
						onChange={(el) => {
							if (el.target.value !== password) {
								el.target.setCustomValidity(
									"Passwords Don't Match"
								)
							} else {
								el.target.setCustomValidity("")
							}
						}}
						required
					/>

					<ReCAPTCHA
						sitekey={CAPTCHA_KEY}
						onChange={(token) => setCaptcha(token || "")}
					/>
					<input
						type="hidden"
						name="captcha"
						value={captcha}
					/>

					<p>
						By creating account, you agree to our{" "}
						<Link to="/tos">Terms of Service</Link>
					</p>
					<button>Sign Up</button>

					<p>
						<Link to="/signin">Already have account</Link>
					</p>
				</form>
			</article>
		</section>
	)
}

export default SignupPage
