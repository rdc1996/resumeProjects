Public Class Form1

    Private isPercent As Boolean = False
    Private isBeingChanged As Boolean = False
    Private equalsClicked As Boolean = False
    Private operand As String = ""
    Private numbers(2) As Double
    Private colorButtons = New Button() {divisionBtn, multiplyBtn, subtractBtn, addBtn}

    Private Sub num1_Click(sender As Object, e As EventArgs) Handles num1.Click

        textBox.Text = textBox.Text + "1"
        If isPercent Or isBeingChanged Then
            textBox.Text = "1"
            isPercent = False
            isBeingChanged = False
        End If
        equalsClicked = False

    End Sub

    Private Sub num2_Click(sender As Object, e As EventArgs) Handles num2.Click

        textBox.Text = textBox.Text + "2"
        If isPercent Or isBeingChanged Then
            textBox.Text = "2"
            isPercent = False
            isBeingChanged = False
        End If
        equalsClicked = False

    End Sub

    Private Sub num3_Click(sender As Object, e As EventArgs) Handles num3.Click

        textBox.Text = textBox.Text + "3"
        If isPercent Or isBeingChanged Then
            textBox.Text = "3"
            isPercent = False
            isBeingChanged = False
        End If
        equalsClicked = False

    End Sub

    Private Sub num4_Click(sender As Object, e As EventArgs) Handles num4.Click

        textBox.Text = textBox.Text + "4"
        If isPercent Or isBeingChanged Then
            textBox.Text = "4"
            isPercent = False
            isBeingChanged = False
        End If
        equalsClicked = False

    End Sub

    Private Sub num5_Click(sender As Object, e As EventArgs) Handles num5.Click

        textBox.Text = textBox.Text + "5"
        If isPercent Or isBeingChanged Then
            textBox.Text = "5"
            isPercent = False
            isBeingChanged = False
        End If
        equalsClicked = False

    End Sub

    Private Sub num6_Click(sender As Object, e As EventArgs) Handles num6.Click

        textBox.Text = textBox.Text + "6"
        If isPercent Or isBeingChanged Then
            textBox.Text = "6"
            isPercent = False
            isBeingChanged = False
        End If
        equalsClicked = False

    End Sub

    Private Sub num7_Click(sender As Object, e As EventArgs) Handles num7.Click

        textBox.Text = textBox.Text + "7"
        If isPercent Or isBeingChanged Then
            textBox.Text = "7"
            isPercent = False
            isBeingChanged = False
        End If
        equalsClicked = False

    End Sub

    Private Sub num8_Click(sender As Object, e As EventArgs) Handles num8.Click

        textBox.Text = textBox.Text + "8"
        If isPercent Or isBeingChanged Then
            textBox.Text = "8"
            isPercent = False
            isBeingChanged = False
        End If
        equalsClicked = False

    End Sub

    Private Sub num9_Click(sender As Object, e As EventArgs) Handles num9.Click

        textBox.Text = textBox.Text + "9"
        If isPercent Or isBeingChanged Then
            textBox.Text = "9"
            isPercent = False
            isBeingChanged = False
        End If
        equalsClicked = False

    End Sub

    Private Sub num0_Click(sender As Object, e As EventArgs) Handles num0.Click

        textBox.Text = textBox.Text + "0"
        If isPercent Or isBeingChanged Then
            textBox.Text = "0"
            isPercent = False
            isBeingChanged = False
        End If
        equalsClicked = False

    End Sub

    Private Sub decimalBtn_Click(sender As Object, e As EventArgs) Handles decimalBtn.Click

        textBox.Text = textBox.Text + "."
        equalsClicked = False

    End Sub

    Private Sub clearBtn_Click(sender As Object, e As EventArgs) Handles clearBtn.Click

        textBox.Text = ""
        equalsClicked = False
        resetNumbers()

    End Sub

    Private Sub negateBtn_Click(sender As Object, e As EventArgs) Handles negateBtn.Click

        Dim finalText As String = textBox.Text
        Dim negated As String = finalText.Substring(0, 1)
        If negated = "-" Then
            finalText = finalText.Substring(1)
        Else
            finalText = "-" + finalText
        End If

        textBox.Text = finalText
        equalsClicked = False

    End Sub

    Private Sub percentBtn_Click(sender As Object, e As EventArgs) Handles percentBtn.Click

        Dim finalText As String = textBox.Text
        If Len(finalText) = 0 Then
            finalText = finalText
        ElseIf Len(finalText) = 1 Then
            finalText = "0.0" + finalText
        ElseIf Len(finalText) = 2 Then
            finalText = "0." + finalText
        Else
            Dim beginning As String = finalText.Substring(0, Len(finalText) - 2)
            Dim ending As String = finalText.Substring(Len(finalText) - 2)
            finalText = beginning + "." + ending
        End If

        textBox.Text = finalText
        isPercent = True
        equalsClicked = False

    End Sub

    Private Sub divisionBtn_Click(sender As Object, e As EventArgs) Handles divisionBtn.Click

        'changeButtonColor(divisionBtn, Color.White)
        If numbers(0) = 0 Then
            numbers(0) = textBox.Text
        Else
            numbers(1) = textBox.Text
            secondEqualsMethod()
        End If
        operand = "/"
        isBeingChanged = True
        equalsClicked = False

    End Sub

    Private Sub multiplyBtn_Click(sender As Object, e As EventArgs) Handles multiplyBtn.Click

        'changeButtonColor(divisionBtn, Color.White)
        If numbers(0) = 0 Then
            numbers(0) = textBox.Text
        Else
            numbers(1) = textBox.Text
            secondEqualsMethod()
        End If
        isBeingChanged = True
        operand = "X"
        equalsClicked = False

    End Sub

    Private Sub subtractBtn_Click(sender As Object, e As EventArgs) Handles subtractBtn.Click

        'changeButtonColor(divisionBtn, Color.White)
        If numbers(0) = 0 Then
            numbers(0) = textBox.Text
        Else
            numbers(1) = textBox.Text
            secondEqualsMethod()
        End If
        isBeingChanged = True
        operand = "-"
        equalsClicked = False

    End Sub

    Private Sub addBtn_Click(sender As Object, e As EventArgs) Handles addBtn.Click

        'changeButtonColor(divisionBtn, Color.White)
        If numbers(0) = 0 Then
            numbers(0) = textBox.Text
        Else
            numbers(1) = textBox.Text
            secondEqualsMethod()
        End If
        operand = "+"
        isBeingChanged = True
        equalsClicked = False

    End Sub

    Private Sub equalBtn_Click(sender As Object, e As EventArgs) Handles equalBtn.Click

        'resetButtonColor()
        Dim firstNum As Double = numbers(0)
        Dim secondNum As Double = textBox.Text
        If equalsClicked Then
            secondNum = numbers(1)
        End If
        Dim finalAnswer As Double = 0
        If operand = "/" Then
            finalAnswer = firstNum / secondNum
        ElseIf operand = "X" Then
            finalAnswer = firstNum * secondNum
        ElseIf operand = "-" Then
            finalAnswer = firstNum - secondNum
        Else
            finalAnswer = firstNum + secondNum
        End If

        textBox.Text = finalAnswer
        numbers(1) = secondNum
        numbers(0) = finalAnswer
        equalsClicked = True

    End Sub

    Private Sub secondEqualsMethod()

        Dim firstNum As Double = numbers(0)
        Dim secondNum As Double = textBox.Text
        If equalsClicked Then
            secondNum = numbers(1)
        End If
        Dim finalAnswer As Double = 0
        If operand = "/" Then
            finalAnswer = firstNum / secondNum
        ElseIf operand = "X" Then
            finalAnswer = firstNum * secondNum
        ElseIf operand = "-" Then
            finalAnswer = firstNum - secondNum
        Else
            finalAnswer = firstNum + secondNum
        End If

        textBox.Text = finalAnswer
        numbers(1) = secondNum
        numbers(0) = finalAnswer
        equalsClicked = True

    End Sub

    Private Sub resetNumbers()
        For number = 0 To 2
            numbers(number) = 0
        Next
    End Sub

    Private Sub changeButtonColor(ByVal button As Button, ByVal color As Color)

        button.BackColor = color

    End Sub

    Private Sub resetButtonColor()
        For Each btn As Button In colorButtons
            btn.BackColor = Color.Transparent
        Next
    End Sub

    Private Sub resetButtonColorExceptThis(ByVal button As Button)
        For Each btn As Button In colorButtons
            If btn.Equals(button) Then
                Continue For
            End If
            btn.BackColor = Color.Transparent
        Next
    End Sub

End Class