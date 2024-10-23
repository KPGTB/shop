import {
	createRef,
	DetailedHTMLProps,
	FormHTMLAttributes,
	PropsWithChildren,
} from "react"

const FetchForm = ({
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
			onSubmit={(event) => {
				event.preventDefault()

				let data = new FormData(ref.current!)

				fetch(API_URL + delegated.action, {
					method: delegated.method,
					credentials: "include",
					body: data,
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

export default FetchForm
