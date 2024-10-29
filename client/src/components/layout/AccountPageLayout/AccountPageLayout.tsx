import {Link, Outlet} from "react-router-dom"

import styles from "./AccountPageLayout.module.scss"

const AccountPageLayout = ({links}: {links: LayoutLink[]}) => {
	return (
		<section className={styles.container}>
			<nav className={styles.nav}>
				{links.map((link) => (
					<Link
						to={link.to}
						key={link.label}
					>
						{link.label}
					</Link>
				))}

				<a href={`${API_URL}/auth/signout`}>Sign Out</a>
			</nav>
			<article className={styles.outlet}>
				<Outlet />
			</article>
		</section>
	)
}

type LayoutLink = {
	to: string
	label: string
}

export default AccountPageLayout
export type {LayoutLink}
