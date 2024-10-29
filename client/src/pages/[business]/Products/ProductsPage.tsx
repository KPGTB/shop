import {useState} from "react"
import {useLoaderData} from "react-router-dom"

import CategoryComp, {
	CategoryCreate,
} from "../../../components/[business]/[product]/Cateogory/CategoryComp"
import CategoryModal from "../../../components/[business]/[product]/Cateogory/CategoryModal"

const BusinessProductsPage = () => {
	const [categories, taxes] = useLoaderData() as [CategoryDto[], TaxCode[]]

	const [categoryModalState, setCategoryModal] = useState<{
		visible: boolean
		data?: CategoryDto
	}>({visible: false, data: undefined})

	return (
		<>
			{categories.map((category) => (
				<CategoryComp
					category={category}
					key={category.id}
					taxes={taxes}
					edit={() =>
						setCategoryModal({visible: true, data: category})
					}
				/>
			))}

			<CategoryCreate
				create={() =>
					setCategoryModal({visible: true, data: undefined})
				}
			/>

			<CategoryModal
				visible={categoryModalState.visible}
				data={categoryModalState.data}
				hide={() => setCategoryModal({visible: false, data: undefined})}
			/>
		</>
	)
}

export default BusinessProductsPage
