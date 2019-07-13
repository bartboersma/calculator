var EventBus = new Vue();

Vue.component('input-first', {
    data: function () {
        return {
            firstNumber: "",
        }
    },
    mounted: function() {
        EventBus.$on('setResult', (previousResult) => {
            this.firstNumber = previousResult;
            this.updateFirstNumber();
        });
    },


    template: `
        <input placeholder="Enter first number" v-model="firstNumber" v-on:change="updateFirstNumber()">
        `,

    methods:{
        updateFirstNumber(){
            EventBus.$emit('firstNumber', this.firstNumber);
        }
    },
});

Vue.component('input-second', {
    data: function () {
        return {
            secondNumber: "",
        }
    },
    mounted: function() {
        EventBus.$on('resetSecondNumber', (empty) => {
            this.secondNumber = empty;
        });
    },

    template: `
        <input placeholder="Enter second number" v-model="secondNumber" v-on:change="updateSecondNumber()">
        `,

    methods:{
        updateSecondNumber(){
            EventBus.$emit('secondNumber', this.secondNumber);
        }
    },
});

Vue.component('calculation-type', {
    data: function () {
        return {
            calculationType: "",
        }
    },
    template: `
        <select v-model="calculationType" v-on:change="updateCalculationType()">
            <option value="SUM">SUM</option>
            <option value="DEDUCT">DEDUCT</option>
            <option value="TIMES">TIMES</option>
            <option value="POWER">POWER</option>
            <option value="ROOT">ROOT</option>
            <option value="DIVIDE">DIVIDE</option>
        </select>
        `,

    methods:{
        updateCalculationType(){
            EventBus.$emit('calculationType', this.calculationType);
        }
    },
});


Vue.component('confirm-button', {
    data: function () {
        return {
            firstNumber: "",
            secondNumber: "",
            calculationType: ""
        }
    },
    mounted: function() {
        EventBus.$on('firstNumber', (firstNumber) => {
            this.firstNumber = Number(firstNumber);
        });

        EventBus.$on('secondNumber', (secondNumber) => {
            this.secondNumber = Number(secondNumber);
        });

        EventBus.$on('calculationType', (calculationType) => {
            this.calculationType = calculationType;
        });
    },
    template: `
        <button type="button" v-on:click="calculate()">Calculate</button>
        `,

    methods:{
        calculate(){
            fetch('api/calculator/calculate', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({firstNumber: this.firstNumber, secondNumber: this.secondNumber, calculationType: this.calculationType})
            })
                .then(function(response){
                    if (response.status === 200) {
                        return response.json();
                    }
                })
                .then(function(data) {
                    if (data === 'undefined') {
                        return;
                    } else {
                        EventBus.$emit('setResult', data);
                        EventBus.$emit('resetSecondNumber', "");
                    }
                });
        }
    }
});

var app = new Vue({
    el: '#app'
});
