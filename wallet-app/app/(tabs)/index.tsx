import { useEffect, useState } from 'react';
import { Button, Text, View } from 'react-native';
import api from '../services/api';

export default function HomeScreen() {
  const [balance, setBalance] = useState<number | null>(null);

  const fetchBalance = () => {
    api.get('/wallets/1/balance')
      .then(res => {
        console.log('Balance response:', res.data);
        setBalance(res.data.balance);
      })
      .catch(err => {
        console.error('API error:', err.response?.data.balance || err.message);
      });
  };


  const deposit = () => {
    api.post('/wallets/1/deposit', { amount: 50.00 })
      .then(() => fetchBalance())
      .catch(err => console.error(err));
  };

  useEffect(() => {
    fetchBalance();
  }, []);

  return (
    <View style={{ padding: 40 }}>
      <Text style={{ fontSize: 18 }}>Wallet Balance</Text>
      <Text style={{ fontSize: 28, marginBottom: 20 }}>
        {balance !== null ? balance : 'Loading...'}
      </Text>

      <Button title="Deposit $50" onPress={deposit} />
    </View>
  );
}
