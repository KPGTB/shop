import "./Accessibility.scss"

import {Accessibility} from "accessibility"
import {useEffect} from "react"

const AccessibilityProvider = () => {
	useEffect(() => {
		new Accessibility()
	}, [])
	return <></>
}
export default AccessibilityProvider
