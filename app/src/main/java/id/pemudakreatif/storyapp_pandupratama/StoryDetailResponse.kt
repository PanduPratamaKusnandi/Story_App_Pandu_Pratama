package id.pemudakreatif.storyapp_pandupratama

data class StoryDetailResponse(
    val error: Boolean,
    val message: String,
    val story: Story
)