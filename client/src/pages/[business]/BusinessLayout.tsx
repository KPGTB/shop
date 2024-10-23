import {Link, Outlet} from "react-router-dom"

import styles from "./BusinessLayout.module.scss"

const BusinessLayout = () => {
	return (
		<section className={styles.container}>
			<nav className={styles.nav}>
				<Link to="/business/dashboard">Dashboard</Link>
				<Link to="/business/products">Products</Link>
				<Link to="/business/discounts">Discounts</Link>
				<Link to="/business/orders">Orders</Link>
				<Link to="/business/apparance">Apparance</Link>
				<Link to="/business/settings">Settings</Link>
				<a href={`${API_URL}/auth/signout`}>Sign Out</a>
			</nav>
			<article className={styles.outlet}>
				<Outlet />
			</article>
		</section>
	)
}

export default BusinessLayout
