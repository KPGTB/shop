import {IconType} from "react-icons"
import {FaDiscord, FaInstagram, FaX} from "react-icons/fa6"
import {MdEmail} from "react-icons/md"
import {Link} from "react-router-dom"

import styles from "./Footer.module.scss"

const year = new Date().getFullYear()
const media: {url: string; text: string; icon: IconType}[] = [
	{
		url: "https://discord.com",
		text: "Discord",
		icon: FaDiscord,
	},
	{
		url: "https://x.com",
		text: "X (Twitter)",
		icon: FaX,
	},
	{
		url: "https://instagram.com",
		text: "Instagram",
		icon: FaInstagram,
	},
]

const Footer = () => {
	return (
		<footer className={styles.footer}>
			<section className={styles.info}>
				<h3>Example Shop Ltd.</h3>
				Example St. 43
				<br />
				00-000 City, Country
				<br />
				Tax Number: 0000000000
			</section>
			<section className={styles.links}>
				<h3>Useful links:</h3>
				<ul>
					<li>
						<Link to="/tos">Terms of Service</Link>
					</li>
					<li>
						<Link to="/privacy">Privacy Policy</Link>
					</li>
					<li>
						<Link to="/map">Site Map</Link>
					</li>
					<li>
						<a
							href="https://kpgtb.eu"
							target="_blank"
						>
							Portfolio
						</a>
					</li>
				</ul>
			</section>
			<section className={styles.follow}>
				<h3>Newsletter</h3>
				<form>
					<input
						type="email"
						name="email"
						placeholder="Your e-mail"
						required
					/>
					<button>
						<MdEmail />
					</button>
				</form>
				<h3>Follow us on</h3>
				<section className={styles.media}>
					{media.map((info) => (
						<a
							href={info.url}
							key={info.url}
							title={info.text}
							target="_blank"
						>
							<info.icon />
						</a>
					))}
				</section>
			</section>
			<section className={styles.copy}>
				Example Shop 2024{year === 2024 ? "" : ` - ${year}`} &copy; All
				rights reserved
			</section>
		</footer>
	)
}

export default Footer
