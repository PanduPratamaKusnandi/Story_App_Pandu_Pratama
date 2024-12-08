package id.pemudakreatif.storyapp_pandupratama

data class StoryResponse(
    val error: Boolean,
    val message: String,
    val listStory: List<Story>
)