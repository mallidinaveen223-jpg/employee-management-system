
import { API_BASE_URL } from '../config/api'

const AUTH_API = `${API_BASE_URL}/api/auth`

export async function loginUser(email, password) {
    const response = await fetch(`${AUTH_API}/login`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({ email, password }),
    });

    const data = await response.json();

    if (!response.ok) {
        throw new Error(data.message || 'Login failed');
    }

    return data;
}

export async function registerUser(formData) {
    const response = await fetch(`${AUTH_API}/register`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({
            fullName: formData.fullName,
            email: formData.email,
            mobile: formData.mobile,
            password: formData.password,
        }),
    });

    const data = await response.json();

    if (!response.ok) {
        throw new Error(data.message || 'Registration failed');
    }

    return data;
}
