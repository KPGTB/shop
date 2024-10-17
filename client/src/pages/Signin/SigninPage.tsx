import {useContext, useEffect, useState} from "react"
import {MdEmail} from "react-icons/md"
import {RiLockPasswordFill} from "react-icons/ri"
import {Link, useSearchParams} from "react-router-dom"
import {Bounce, toast} from "react-toastify"

import ThemeContext from "../../context/ThemeContect"
import {Theme} from "../Layout"
import styles from "./SigninPage.module.scss"

const SigninPage = () => {
	const [accType, setAccType] = useState<string>("customer")
	const [params, setParams] = useSearchParams()
	const theme = useContext<Theme>(ThemeContext)

	useEffect(() => {
		if (params.has("error")) {
			toast.error("Incorrect e-mail or password!", {
				position: "bottom-right",
				autoClose: 5000,
				hideProgressBar: false,
				closeOnClick: false,
				pauseOnHover: true,
				draggable: false,
				progress: undefined,
				theme: theme,
				transition: Bounce,
			})
		}
	}, [])

	return (
		<section className={styles.main}>
			<article className={styles.container}>
				<form
					className={styles.form}
					method="POST"
					action="https://localhost:8080/login"
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
						<label htmlFor="remember">Customer</label>
						<input
							name="type"
							type="radio"
							id="business"
							value="business"
							checked={accType === "business"}
							onChange={(el) => setAccType(el.target.value)}
						/>{" "}
						<label htmlFor="remember">Business</label>
					</aside>

					{accType === "customer" && (
						<>
							<label htmlFor="email">
								<MdEmail /> E-Mail
							</label>
							<input
								name="username"
								type="email"
								id="email"
							/>
						</>
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
