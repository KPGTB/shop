import {useContext} from "react"
import {IconType} from "react-icons"
import {FaDiscord, FaHeart, FaStar, FaUser} from "react-icons/fa"
import {FaCartShopping} from "react-icons/fa6"

import ThemeContext from "../../context/ThemeContext"
import {Theme} from "../Layout"
import styles from "./LandingPage.module.scss"

const features: {
	icon: IconType
	title: string
	description: string
}[] = [
	{
		icon: FaHeart,
		title: "Feature 1",
		description:
			"Ea mollit incididunt voluptate minim anim nisi magna veniam nisi tempor non aute culpa nostrud.",
	},
	{
		icon: FaStar,
		title: "Feature 2",
		description:
			"Sit ullamco fugiat amet consectetur veniam. In consectetur et voluptate occaecat ea commodo id enim excepteur commodo quis id.",
	},
	{
		icon: FaUser,
		title: "Feature 3",
		description:
			"Adipisicing minim dolor non deserunt fugiat ullamco ut eiusmod veniam.",
	},
	{
		icon: FaDiscord,
		title: "Feature 4",
		description: "Lorem excepteur aliquip enim velit.",
	},
]

const LandingPage = () => {
	const theme = useContext<Theme>(ThemeContext)

	return (
		<>
			<section
				style={{backgroundImage: `url("/landing-${theme}.png")`}}
				className={styles.header}
			>
				<section>
					<h1>My Awesome Shop</h1>
					<p>
						Ut ad deserunt sint do. Ipsum commodo nisi est fugiat
						nisi nulla do non et. Est nisi ea dolor anim adipisicing
						excepteur ea commodo labore Lorem laborum officia.
						Excepteur tempor in occaecat qui cupidatat elit et
						veniam sit occaecat incididunt cupidatat officia.
						Eiusmod minim officia mollit cupidatat ipsum elit ea
						irure elit quis pariatur non Lorem ea. Proident officia
						exercitation aliquip eu eu dolor laborum sunt consequat
						sunt minim nisi nisi proident.
					</p>
				</section>
			</section>
			<article className={styles.features}>
				{features.map((feat) => (
					<section
						key={feat.title}
						className={styles.feature}
					>
						<section className={styles.icon}>
							<feat.icon />
						</section>

						<h3>{feat.title}</h3>
						<p>{feat.description}</p>
					</section>
				))}
			</article>
			<article className={styles.stats}>
				<section>
					<FaUser />
					<br />
					1000+ Accounts Created
				</section>
				<section>
					<FaCartShopping />
					<br />
					12000+ Products purchased
				</section>
				<section>
					<FaStar />
					<br />
					4.9/5.0 Reviews
				</section>
			</article>
		</>
	)
}

export default LandingPage
