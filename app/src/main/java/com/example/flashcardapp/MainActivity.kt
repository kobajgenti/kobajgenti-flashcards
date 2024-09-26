package com.example.flashcardapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.flashcardapp.ui.theme.FlashcardappTheme
import kotlinx.coroutines.launch

data class Flashcard(val question: String, val answer: String)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FlashcardappTheme {
                FlashcardQuizApp()
            }
        }
    }
}

@Composable
fun FlashcardQuizApp() {
    // Define the list of flashcards
    val flashcards = listOf(
        Flashcard("What is 2 + 2?", "4"),
        Flashcard("What is the color of the sky?", "Blue"),
        Flashcard("What is the capital of France?", "Paris"),
        Flashcard("Who is the best professor in the world?", "Ronald Czik")
    )
    // State variables
    val currentQuestionIndex = remember { mutableStateOf(0) }
    val userAnswer = remember { mutableStateOf("") }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val quizComplete = currentQuestionIndex.value >= flashcards.size

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.imePadding() // this took time to fix!
        ) }
    ) { innerPadding ->
        if (quizComplete) {
            // Show "Quiz Complete" Snackbar
            LaunchedEffect(Unit) {
                coroutineScope.launch {
                    snackbarHostState.showSnackbar("Quiz Complete!")
                }
            }
            // Quiz complete UI
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Quiz Complete!", style = MaterialTheme.typography.headlineMedium)
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = {
                    // Reset state to restart quiz
                    currentQuestionIndex.value = 0
                    userAnswer.value = ""
                }) {
                    Text("Restart Quiz")
                }
            }
        } else {
            // Show current question
            val currentFlashcard = flashcards[currentQuestionIndex.value]
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Text(
                        text = currentFlashcard.question,
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.titleLarge
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                TextField(
                    value = userAnswer.value,
                    onValueChange = { userAnswer.value = it },
                    label = { Text("Your Answer") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        // Validate input
                        if (userAnswer.value.trim().isEmpty()) {
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Please enter an answer.")
                            }
                        } else {
                            // Check if the answer is correct
                            val correctAnswer = currentFlashcard.answer.trim().lowercase()
                            val userAnswerText = userAnswer.value.trim().lowercase()
                            if (userAnswerText == correctAnswer) {
                                // Correct answer
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar("Correct!")
                                }
                                // Move to next question
                                currentQuestionIndex.value += 1
                                // Clear the user's answer
                                userAnswer.value = ""
                            } else {
                                // Incorrect answer
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar("Incorrect, try again.")
                                }
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Submit Answer")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FlashcardQuizAppPreview() {
    FlashcardappTheme {
        FlashcardQuizApp()
    }
}