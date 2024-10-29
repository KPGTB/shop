import {useContext, useEffect, useState} from "react"
import {FaRegUserCircle} from "react-icons/fa"
import {FaCartShopping} from "react-icons/fa6"
import {MdError, MdModeNight, MdWbSunny} from "react-icons/md"
import {Link} from "react-router-dom"

import {useAuth} from "../../../context/AuthContext"
import {useCart} from "../../../context/CartContext"
import ThemeContext from "../../../context/ThemeContext"
import styles from "./Header.module.scss"
import headerLoader from "./HeaderLoader"

const Header = ({setTheme}: {setTheme: (newTheme: Theme) => void}) => {
	const theme = useContext<Theme>(ThemeContext)
	const [logged, user] = useAuth()
	const [cartItems] = useCart()
	const [categories, setCategories] = useState<CategoryDto[] | undefined>([])

	useEffect(() => {
		headerLoader()
			.then((data) => setCategories(data))
			.catch((e) => setCategories(undefined))
	}, [])

	return (
		<>
			<header className={styles.header}>
				<Link
					to="/"
					className={styles.home}
				>
					<img
						src="/banner.png"
						alt="logo"
					/>
				</Link>

				<nav>
					<section className={styles.links}>
						{categories?.map((cat) => (
							<Link
								to={`/shop/${cat.nameInUrl}.${cat.id}`}
								key={cat.id}
							>
								{cat.name}
							</Link>
						))}
						<Link to="/contact">Contact Us</Link>
					</section>
					<section className={styles.manage}>
						<button
							onClick={() => {
								setTheme(theme === "light" ? "dark" : "light")
							}}
							className="icon"
						>
							{theme === "light" ? (
								<MdWbSunny />
							) : (
								<MdModeNight />
							)}
						</button>
						<Link
							to="cart"
							className={styles.cart}
						>
							<FaCartShopping />
							{cartItems.length > 0 && (
								<span>{cartItems.length}</span>
							)}
						</Link>
						<Link
							to={
								user === undefined
									? "/signin"
									: `/${user.role.toLowerCase()}/dashboard`
							}
						>
							<FaRegUserCircle />
						</Link>
					</section>
				</nav>
			</header>
			{categories === undefined && (
				<aside className={styles.apiError}>
					<MdError /> An unexpected error occurred. The API did not
					respond. The site may not function properly...
				</aside>
			)}
		</>
	)
}

export default Header
