document.addEventListener("DOMContentLoaded", function () {
    if (typeof bookedDates === 'undefined') {
        console.error("bookedDates не определены");
        return;
    }

    const checkInPicker = flatpickr("#checkInDate", {
        dateFormat: "Y-m-d",
        disable: bookedDates,
        minDate: "today",
        onChange: function(selectedDates, dateStr) {
            checkOutPicker.set("minDate", dateStr);
        }
    });

    const checkOutPicker = flatpickr("#checkOutDate", {
        dateFormat: "Y-m-d",
        disable: bookedDates,
        minDate: "today"
    });

    function validateBookingForm(formData, bookedDates) {
        const errors = [];
        const roomId = formData.get("roomId");
        const checkInDate = formData.get("checkInDate");
        const checkOutDate = formData.get("checkOutDate");

        if (!roomId || !checkInDate || !checkOutDate) {
            errors.push("Все поля должны быть заполнены!");
        }

        if (checkInDate && checkOutDate && new Date(checkInDate) >= new Date(checkOutDate)) {
            errors.push("Дата выезда должна быть позже даты заезда!");
        }

        const today = new Date();
        today.setHours(0, 0, 0, 0);

        if (checkInDate && new Date(checkInDate) < today) {
            errors.push("Дата заезда не может быть в прошлом!");
        }

        if (bookedDates && Array.isArray(bookedDates)) {
            const selectedDates = getDatesBetween(checkInDate, checkOutDate);
            const isBooked = selectedDates.some(date => bookedDates.includes(date));

            if (isBooked) {
                errors.push("Выбранные даты уже заняты!");
            }
        }

        return errors;
    }

    function getDatesBetween(startDate, endDate) {
        const dates = [];
        let currentDate = new Date(startDate);
        const end = new Date(endDate);

        while (currentDate <= end) {
            dates.push(currentDate.toISOString().split('T')[0]);
            currentDate.setDate(currentDate.getDate() + 1);
        }

        return dates;
    }

    const csrfToken = document.querySelector('meta[name="_csrf"]').getAttribute('content');
    const csrfHeader = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');

    const form = document.querySelector('.booking-form');
    const messageBox = document.getElementById('booking-message');

    form.addEventListener('submit', function (event) {
        event.preventDefault();

            const formData = new FormData(form);
            const validationErrors = validateBookingForm(formData, bookedDates);

            for (let [key, value] of formData.entries()) {
                console.log(key, "=", value);
            }

            if (validationErrors.length > 0) {
                messageBox.textContent = validationErrors.join("\n");
                messageBox.style.color = "red";
                return;
            }

            fetch("/booking/ajax", {
                method: "POST",
                headers: {
                    [csrfHeader]: csrfToken
                },
                body: formData
            })
            .then(response => response.text())
            .then(result => {
                messageBox.textContent = result;
                messageBox.style.color = "green";
            })
            .catch(error => {
                messageBox.textContent = "Ошибка: " + error.message;
                messageBox.style.color = "red";
                console.error("Ошибка:", error);
            });
    });

});
