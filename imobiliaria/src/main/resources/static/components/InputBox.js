export default {
    props: {
        label: String,
        placeholder: String,
        type: {
            type: String,
            default: 'text'
        },
        erro: String,
        modelValue: String
    },
    emits: ['update:modelValue'],
    template: `
        <div class="login-input-box">
            <label>{{ label }}</label>
            <input 
                :type="type" 
                :value="modelValue"
                @input="$emit('update:modelValue', $event.target.value)"
                :placeholder="placeholder" 
                autocomplete="off"
            >
            <p v-show="erro" class="login-input-invalido">{{ erro }}</p>
        </div>
    `
}