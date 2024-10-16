import {FaRegUserCircle} from "react-icons/fa"
import {FaCartShopping} from "react-icons/fa6"
import {MdModeNight, MdWbSunny} from "react-icons/md"
import {Link} from "react-router-dom"

import {Theme} from "../../../pages/layout"
import styles from "./Header.module.scss"

const Header = ({
	theme,
	setTheme,
}: {
	theme: string
	setTheme: (newTheme: Theme) => void
}) => {
	return (
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
					<Link to="/shop">E-Books</Link>
					<Link to="/shop">Ranks</Link>
					<Link to="/contact">Contact Us</Link>
				</section>
				<section className={styles.manage}>
					<button
						onClick={() => {
							setTheme(theme === "light" ? "dark" : "light")
						}}
					>
						{theme === "light" ? <MdWbSunny /> : <MdModeNight />}
					</button>
					<Link
						to="cart"
						className={styles.cart}
					>
						<FaCartShopping />
						<span>3</span>
					</Link>
					<Link to="/signin">
						<FaRegUserCircle />
					</Link>
				</section>
			</nav>
		</header>
	)
}

export default Header
