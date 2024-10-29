import {
	createRef,
	DetailedHTMLProps,
	FormHTMLAttributes,
	PropsWithChildren,
} from "react"

const JsonForm = ({
	after,
	reload = true,
	children,
	...delegated
}: {
	after?: (response: Response) => void
	reload?: boolean
} & PropsWithChildren &
	DetailedHTMLProps<
		FormHTMLAttributes<HTMLFormElement>,
		HTMLFormElement
	>) => {
	const ref = createRef<HTMLFormElement>()

	return (
		<form
			ref={ref}
			className={delegated.className}
			onSubmit={(event) => {
				event.preventDefault()

				let data = new FormData(ref.current!)

				fetch(API_URL + delegated.action, {
					method: delegated.method,
					credentials: "include",
					headers: {
						"Content-Type": "application/json",
					},
					body: JSON.stringify(form2json(data)),
				}).then((res) => {
					if (after !== undefined) after(res)
					if (reload) window.location.reload()
				})
			}}
		>
			{children}
		</form>
	)
}

function form2json(data: FormData) {
	let method = function (
		object: {[key: string]: any},
		pair: [string, any]
	): {[key: string]: any} {
		let keys = pair[0].replace(/\]/g, "").split("[")
		let key = keys[0]
		let value = pair[1]

		if (keys.length > 1) {
			let i, x, segment
			let last = value
			let type = isNaN(Number(keys[1])) ? {} : []

			value = segment = object[key] || type

			for (i = 1; i < keys.length; i++) {
				x = keys[i]

				if (i == keys.length - 1) {
					if (Array.isArray(segment)) {
						segment.push(last)
					} else {
						segment[x] = last
					}
				} else if (segment[x] == undefined) {
					segment[x] = isNaN(Number(keys[i + 1])) ? {} : []
				}

				segment = segment[x]
			}
		}

		object[key] = value

		return object
	}

	let object = Array.from(data).reduce(method, {})

	return object
}

export default JsonForm
